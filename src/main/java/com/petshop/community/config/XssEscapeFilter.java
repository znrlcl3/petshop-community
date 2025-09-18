package com.petshop.community.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class XssEscapeFilter implements Filter {

    private List<String> excludeUrls;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String excludeParam = filterConfig.getInitParameter("excludeUrls");
        if (excludeParam != null && !excludeParam.isEmpty()) {
            excludeUrls = Arrays.asList(excludeParam.split(","));
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // 보안 헤더 설정
        setSecurityHeaders(httpResponse);
        
        String uri = httpRequest.getRequestURI();
        
        // 제외 URL 체크
        if (shouldExcludeUrl(uri)) {
            chain.doFilter(request, response);
            return;
        }
        
        // XSS 필터링 적용
        XssEscapeRequestWrapper wrappedRequest = new XssEscapeRequestWrapper(httpRequest);
        chain.doFilter(wrappedRequest, response);
    }

    private void setSecurityHeaders(HttpServletResponse response) {
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
    }

    private boolean shouldExcludeUrl(String uri) {
        if (excludeUrls == null || excludeUrls.isEmpty()) {
            return false;
        }
        
        return excludeUrls.stream()
                .anyMatch(excludeUrl -> uri.startsWith(excludeUrl.replace("*", "")));
    }

    @Override
    public void destroy() {
        // 리소스 정리
    }
}