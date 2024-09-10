package com.donghyun.EGG.security;

import com.donghyun.EGG.api.controller.member.response.MemberLoginResponse;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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

    public String checkToken() {
        String accessToken = getJwt();
        log.debug("accessToken: {}", accessToken);
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
            // JWT 토큰 형식 판단 및 서명 검증
            log.debug("claims: {}", claims);

        } catch (ExpiredJwtException e) {
            log.debug("ExpiredJwtException: {}", e);

        } catch (JwtException e) {
            log.debug("JwtException: {}", e);
        }
        return accessToken;
    }

    public String getJwt() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");
        if (token.startsWith("Bearer")) {
            token = token.substring(7);
        }
        return token;

    }


}
