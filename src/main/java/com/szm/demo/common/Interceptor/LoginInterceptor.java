package com.szm.demo.common.Interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.szm.demo.common.BusinessException;
import com.szm.demo.common.RedisKeyConstants;
import com.szm.demo.common.ResultCode;
import com.szm.demo.entity.UserInfo;
import com.szm.demo.mapper.UserInfoMapper;
import com.szm.demo.util.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    final Logger logger = LoggerFactory.getLogger(getClass());

    private final Long EXPIRE_TIME = 30L;

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {

        String token = request.getHeader("Authorization");
        if (token == null || token.isBlank()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户未登录");
        }
        String userId;
        try {
            userId = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException e) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        String key = RedisKeyConstants.USER_INFO.getKey(userId);
        UserInfo userInfo = redisUtil.get(key, UserInfo.class);
        if (userInfo == null) {
            userInfo = userInfoMapper.getById(Long.parseLong(userId));
            if (userInfo == null) {
                throw new BusinessException(ResultCode.USER_NOT_EXIST);
            }
            redisUtil.set(key, userInfo, EXPIRE_TIME, TimeUnit.MINUTES);
        }
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(userInfo.getPassword())).build();
            verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR, "验证失败,请重新登录");
        }
        request.setAttribute("userId", userId);
        logger.info("User: {} 登录成功, token:{}", userInfo.getUsername(), token);
        return true;
    }
}
