package com.donghyun.EGG.config;

import com.donghyun.EGG.security.JwtUtil;
import com.donghyun.EGG.security.MyFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class FilterConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public FilterRegistrationBean<MyFilter> firstFilter() {
        FilterRegistrationBean<MyFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new MyFilter(jwtUtil));
        registrationBean.addUrlPatterns("/stock/*"); // 필터를 적용할 URL 패턴
        registrationBean.setOrder(1); // 필터 순서 설정 (숫자가 낮을수록 우선 순위가 높음)
        return registrationBean;
    }
}
