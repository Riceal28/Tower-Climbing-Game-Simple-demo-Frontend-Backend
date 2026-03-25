package com.szm.demo.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TokenUtil {

    private final String SECRET = "tower-climbing-demo";
    private final Long EXPIRY_TIME = 7 * 24 * 60 * 60 * 1000L;

    public String generateToken(Long userId, String email) {
        return JWT.create()
                .withAudience(userId.toString())
                .withClaim("email", email)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis()+EXPIRY_TIME))
                .sign(Algorithm.HMAC256(SECRET));
    }
}
