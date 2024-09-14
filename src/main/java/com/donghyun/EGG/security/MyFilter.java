package com.donghyun.EGG.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class MyFilter implements Filter {

    private final JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.debug("[doFilter] 필터 들어왔다!");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("[doFilter] JWT 토큰이 없습니다!");
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        String token = authHeader.substring(7); // "Bearer " 부분을 제외한 실제 토큰

        try {
            // JWT 검증
            Claims claims = jwtUtil.validateToken(token);
            log.debug("[doFilter] JWT 토큰 검증 성공: {}", claims.getSubject());

            // 다음 필터로 요청을 넘김
            chain.doFilter(request, response);

        } catch (Exception e) {
            log.error("[doFilter] JWT 토큰 검증 실패: {}", e.getMessage());
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }

        log.debug("[doFilter] 필터 나간다!");

    }
}
