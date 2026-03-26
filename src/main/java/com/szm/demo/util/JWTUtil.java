package com.szm.demo.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.szm.demo.common.BusinessException;
import com.szm.demo.common.ResultCode;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtil {

    private final String SECRET = "tower-climbing-demo";
    private final Long EXPIRY_TIME = 7 * 24 * 60 * 60 * 1000L;

    public String generateToken(Long userId, String email) {
        return JWT.create()
                .withAudience(userId.toString())
                .withClaim("email", email)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRY_TIME))
                .sign(Algorithm.HMAC256(SECRET));
    }

    public DecodedJWT verify(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET))
                .build();
        try {
            return verifier.verify(token);
        } catch (Exception e) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户验证失败");
        }
    }

    public Long getUserId(String token) {
        try {
            return Long.parseLong(verify(token)
                    .getAudience()
                    .get(0));
        } catch (Exception e) {
            throw new BusinessException(ResultCode.UNAUTHORIZED,"用户身份无效");
        }

    }
}
