package com.petshop.community.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.servlet.Filter;

/**
 * XSS 방어 필터 설정
 */
@Configuration
public class XssConfig {

    @Bean
    public FilterRegistrationBean<Filter> xssEscapeFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new XssEscapeFilter());
        registrationBean.setOrder(1);
        registrationBean.addUrlPatterns("/*");
        
        // 제외할 URL 패턴 설정 (선택사항)
        registrationBean.addInitParameter("excludeUrls", "/css/*,/js/*,/images/*,/uploads/*,/favicon.ico");
        
        return registrationBean;
    }
}