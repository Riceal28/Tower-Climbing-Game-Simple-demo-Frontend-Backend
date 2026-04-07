package com.szm.demo.service.Impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.szm.demo.common.BusinessException;
import com.szm.demo.common.RedisKeyConstants;
import com.szm.demo.common.ResultCode;
import com.szm.demo.dto.UserLoginReq;
import com.szm.demo.dto.UserRegisterReq;
import com.szm.demo.entity.UserDetail;
import com.szm.demo.entity.UserInfo;
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
        redisUtil.set(key, userInfo, 1440, TimeUnit.MINUTES);
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
        redisUtil.set(key, "1", 1440, TimeUnit.MINUTES);
        logger.info("User: {} 登录成功, token:{}", userInfo.getUsername(), token);
        return token;
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

    @Override
    public UserDetail getUserDetail(Long userId) {
        if (userId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        String key = RedisKeyConstants.USER_DETAIL.getKey(userId);
        UserDetail userDetail = redisUtil.get(key, UserDetail.class);
        if (userDetail == null) {
            userDetail = userDetailMapper.getByUserId(userId);
            if (userDetail == null) {
                logger.error("用户信息不存在");
                throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
            }
            redisUtil.set(key, userDetail, 1440, TimeUnit.HOURS);
        }
        logger.info("查询了一次用户详情");
        return userDetail;
    }

    @Override
    @Transactional
    public void setUserDetail(UserDetail userDetail) {
        if (userDetail == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        if (userDetail.getUserId() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        Long userId = userDetail.getUserId();
        String key = RedisKeyConstants.USER_DETAIL.getKey(userId);
        try {
            userDetailMapper.updateAllByUserId(userDetail);
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            redisUtil.set(key, userDetail, 1440, TimeUnit.MINUTES);
                        }
                    }
            );
            logger.info("用户ID[{}]:更新信息成功,{}",userId,userDetail);
        } catch (Exception e) {
            logger.error("用户ID[{}]:更新信息失败", userId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    @Override
    @Transactional
    public void setExp(Long userId, Long exp) {
        try {
            userDetailMapper.updateExpByUserId(userId, exp);
            String key = RedisKeyConstants.USER_DETAIL.getKey(userId);
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            redisUtil.delete(key);
                        }
                    }
            );
            logger.info("用户ID[{}]:更新经验成功,设置为{}",userId,exp);
        } catch (Exception e) {
            logger.error("用户ID[{}]:更新经验失败", userId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }

    }
}
