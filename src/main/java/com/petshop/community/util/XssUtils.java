package com.petshop.community.util;

import java.net.URL;

import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import com.nhncorp.lucy.security.xss.XssFilter;

/**
 * Lucy XSS Filter를 활용한 XSS 방어 유틸리티
 */
@Component("xssUtils")
public class XssUtils {
    
    private static XssFilter xssFilter;
    private static XssFilter strictFilter;
    
    static {
        System.out.println("XssUtils 초기화 시작");
        initializeFilters();
        System.out.println("XssUtils 초기화 완료");
    }
    
    private static void initializeFilters() {
        try {
            // 클래스패스 리소스 존재 확인
            ClassLoader classLoader = XssUtils.class.getClassLoader();
            URL resource = classLoader.getResource("lucy-xss-superset.xml");
            System.out.println("설정 파일 경로: " + resource);
            
            if (resource == null) {
                System.err.println("lucy-xss-superset.xml 파일을 찾을 수 없습니다");
                throw new RuntimeException("설정 파일 없음");
            }
            
            xssFilter = XssFilter.getInstance("lucy-xss-strict.xml");
            System.out.println("Lucy 필터 초기화 성공");
            
        } catch (Exception e) {
            System.err.println("Lucy 필터 초기화 실패: " + e.getMessage());
            e.printStackTrace();
            // 기본 설정으로 재시도
            // ...
        }
    }
    
    public static String filterHtml(String input) {
        System.out.println("=== Lucy Filter 디버깅 ===");
        System.out.println("입력: " + input);
        
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        
        if (xssFilter != null) {
            try {
                String result = xssFilter.doFilter(input);
                System.out.println("Lucy 결과: '" + result + "'");
                System.out.println("결과 길이: " + result.length());
                System.out.println("입력과 동일?: " + input.equals(result));
                return result;
            } catch (Exception e) {
                System.err.println("Lucy 필터링 실패: " + e.getMessage());
                return HtmlUtils.htmlEscape(input);
            }
        }
        return HtmlUtils.htmlEscape(input);
    }
    
    /**
     * 엄격한 XSS 필터링 (모든 HTML 태그 제거)
     */
    public static String filterStrict(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        
        if (strictFilter != null) {
            try {
                return strictFilter.doFilter(input);
            } catch (Exception e) {
                System.err.println("Lucy 엄격 필터링 실패: " + e.getMessage());
                return HtmlUtils.htmlEscape(input);
            }
        } else {
            return HtmlUtils.htmlEscape(input);
        }
    }
    
    /**
     * 완전한 HTML 이스케이프 (입력 필드용)
     */
    public static String escapeHtml(String input) {
        return input == null ? null : HtmlUtils.htmlEscape(input);
    }
    
    /**
     * JavaScript에서 안전한 문자열 출력
     */
    public static String escapeJavaScript(String input) {
        return input == null ? null : HtmlUtils.htmlEscapeDecimal(input);
    }
    
    /**
     * URL 파라미터 인코딩
     */
    public static String encodeUrl(String input) {
        if (input == null) {
            return null;
        }
        try {
            return java.net.URLEncoder.encode(input, "UTF-8");
        } catch (Exception e) {
            return input;
        }
    }
    
    /**
     * SQL Injection 방어를 위한 기본 이스케이프
     */
    public static String escapeSql(String input) {
        if (input == null) {
            return null;
        }
        // 기본적인 SQL Injection 패턴 제거
        return input.replaceAll("['\"\\\\;]", "");
    }
}