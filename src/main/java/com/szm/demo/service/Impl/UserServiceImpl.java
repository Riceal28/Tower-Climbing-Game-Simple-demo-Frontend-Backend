package com.szm.demo.service.Impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.szm.demo.common.BusinessException;
import com.szm.demo.common.RedisKeyConstants;
import com.szm.demo.common.ResultCode;
import com.szm.demo.dto.UserLoginReq;
import com.szm.demo.dto.UserRegisterReq;
import com.szm.demo.entity.LevelInfo;
import com.szm.demo.entity.UserDetail;
import com.szm.demo.entity.UserInfo;
import com.szm.demo.mapper.LevelInfoMapper;
import com.szm.demo.mapper.UserDetailMapper;
import com.szm.demo.mapper.UserInfoMapper;
import com.szm.demo.service.UserService;
import com.szm.demo.util.RedisUtil;
import com.szm.demo.util.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    UserDetailMapper userDetailMapper;

    @Autowired
    LevelInfoMapper levelInfoMapper;


    @Override
    public void register(UserRegisterReq req) {
        if (req == null || req.getUsername().isBlank()
                || req.getEmail().isBlank() || req.getPassword().isBlank()) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        UserInfo userInfo = req.toEntity();
        try {
            userInfoMapper.insert(userInfo);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(ResultCode.DATA_DUPLICATE, "该用户已存在");
        }
        String key = RedisKeyConstants.USER_INFO.getKey(userInfo.getId());
        redisUtil.set(key, userInfo, 30, TimeUnit.MINUTES);
        jwtUtil.generateToken(userInfo.getId(), userInfo.getUsername(), userInfo.getEmail());
    }

    @Override
    public String login(UserLoginReq req) {
        if (req == null || req.getUsername().isBlank()
                || req.getPassword().isBlank()) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        //todo:redis登录限流
        UserInfo userInfo = userInfoMapper.getByUsername(req.getUsername());
        if (userInfo == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST, "用户不存在");
        }
        if (!userInfo.getPassword().equals(req.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR, "密码错误");
        }
        String token = jwtUtil.generateToken(userInfo.getId(), userInfo.getUsername(), userInfo.getEmail());
        String jti = jwtUtil.getJTI(token);
        String key = RedisKeyConstants.USER_TOKEN_IN.getKey(jti);
        redisUtil.set(key, "1", 30, TimeUnit.MINUTES);
        logger.info("User: {} 登录成功, token:{}", userInfo.getUsername(), token);
        return token;
    }

    @Override
    @Transactional//抛出异常自动回滚
    public void createDefaultPlayer(Long userId) {
        if (userDetailMapper.getByUserId(userId) != null) {
            throw new BusinessException(ResultCode.DATA_DUPLICATE, "已拥有角色");
        }
        try {
            LevelInfo levelInfo = levelInfoMapper.getByLevel(1);
            UserDetail userDetail = new UserDetail();
            userDetail.setUserId(userId);
            userDetail.setLevel(levelInfo.getLevel());
            userDetail.setExp(0L);
            userDetail.setCurrentHp(levelInfo.getMaxHp());
            userDetail.setCurrentMp(levelInfo.getMaxMp());
            userDetail.setAttackBase(levelInfo.getAttackBase());
            userDetail.setCreateTime(LocalDateTime.now());
            userDetail.setUpdateTime(LocalDateTime.now());
            userDetailMapper.insert(userDetail);

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            String key = RedisKeyConstants.USER_DETAIL.getKey(userId);
                            redisUtil.set(key, userDetail, 30, TimeUnit.MINUTES);
                        }
                    }
            );
        } catch (Exception e) {
            logger.error("用户ID[{}]:创建角色失败", userId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    @Override
    public void logout(String token) {
        DecodedJWT jwt = jwtUtil.verify(token);
        String jti = jwt.getId();
        long ttl = jwt.getExpiresAt().getTime() - System.currentTimeMillis();
        if (ttl <= 0) {
            return;
        }
        String key = RedisKeyConstants.USER_TOKEN_OUT.getKey(jti);
        redisUtil.set(key, "1", ttl, TimeUnit.MILLISECONDS);
        logger.info("User: {} 登出成功, token:{}", jwt.getClaim("username"), token);
    }

}
