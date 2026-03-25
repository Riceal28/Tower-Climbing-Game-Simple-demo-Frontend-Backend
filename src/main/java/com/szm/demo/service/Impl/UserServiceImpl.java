package com.szm.demo.service.Impl;

import com.szm.demo.common.BusinessException;
import com.szm.demo.common.RedisKeyConstants;
import com.szm.demo.common.ResultCode;
import com.szm.demo.dto.UserLoginReq;
import com.szm.demo.dto.UserRegisterReq;
import com.szm.demo.entity.UserInfo;
import com.szm.demo.mapper.UserInfoMapper;
import com.szm.demo.service.UserService;
import com.szm.demo.util.RedisUtil;
import com.szm.demo.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    UserInfoMapper userInfoMapper;


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
        return tokenUtil.generateToken(userInfo.getId(), userInfo.getEmail());
    }
}
