package com.szm.demo.service.Impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.szm.demo.common.BusinessException;
import com.szm.demo.common.RedisKeyConstants;
import com.szm.demo.common.ResultCode;
import com.szm.demo.dto.UserLoginReq;
import com.szm.demo.dto.UserRegisterReq;
import com.szm.demo.entity.UserPlayerInfo;
import com.szm.demo.entity.UserInfo;
import com.szm.demo.mapper.UserPlayerInfoMapper;
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
    UserPlayerInfoMapper userPlayerInfoMapper;

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
        //todo:登录限流(redis或其他)
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

}
