package com.donghyun.EGG.security;

import com.donghyun.EGG.api.controller.member.response.MemberLoginResponse;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final Key key;
    private final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 365; // 1년
    //    private final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 10; // 10초
    private final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 365 * 10; // 10년
    // TODO: 2024-06-26 (026) 만료시간 재 설정

    private JwtUtil(@Value("${JWT_SECRET}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public MemberLoginResponse generateAllToken(String subject, String name) {
        String accessToken = generateToken(subject, name, ACCESS_TOKEN_EXPIRE_TIME);
        String refreshToken = generateToken(subject, name, REFRESH_TOKEN_EXPIRE_TIME);

        log.info("[generateAllToken] 정상적으로 토큰이 발급되었습니다!");
        return MemberLoginResponse.of(accessToken, refreshToken);
    }


    public String generateToken(String subject,String name, long expTime) {
        Date now = new Date();
        Date expDate = new Date(now.getTime() + expTime);

        return Jwts.builder()
                .setIssuer("egg")
                .setSubject(subject)
                .setAudience(name)
                .setExpiration(expDate)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public Claims validateToken(String token) {;

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
