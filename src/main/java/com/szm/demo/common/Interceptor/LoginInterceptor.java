package com.szm.demo.common.Interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.szm.demo.common.BusinessException;
import com.szm.demo.common.ResultCode;
import com.szm.demo.entity.UserInfo;
import com.szm.demo.mapper.UserInfoMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    UserInfoMapper userInfoMapper;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler){

        String token = request.getHeader("Authoritarian");
        if(token.isBlank()){
            throw new BusinessException(ResultCode.UNAUTHORIZED,"用户未登录");
        }
        String userId;
        try{
            userId = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException e){
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        UserInfo userInfo = userInfoMapper.getById(Long.getLong(userId));
        if(userInfo == null){
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        try{
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(userInfo.getPassword())).build();
            verifier.verify(token);
        }catch(JWTVerificationException e){
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }
        request.setAttribute("userId",userId);
        logger.info("User: {} 登录成功, token:{}",userInfo.getUsername(),token);
        return true;
    }
}
