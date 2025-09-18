package com.petshop.community.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.util.HtmlUtils;

import com.nhncorp.lucy.security.xss.XssFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * Lucy XssFilter를 사용한 XSS 방어 RequestWrapper
 */
public class XssEscapeRequestWrapper extends HttpServletRequestWrapper {

    private static final Map<String, XssFilter> FILTER_CACHE = new HashMap<>();
    private XssFilter xssFilter;

    public XssEscapeRequestWrapper(HttpServletRequest request) {
        super(request);
        initializeXssFilter("lucy-xss-superset.xml");
    }

    public XssEscapeRequestWrapper(HttpServletRequest request, String configFile) {
        super(request);
        initializeXssFilter(configFile);
    }

    private synchronized void initializeXssFilter(String configFile) {
        try {
            // 캐시에서 필터 인스턴스 조회
            if (FILTER_CACHE.containsKey(configFile)) {
                xssFilter = FILTER_CACHE.get(configFile);
                return;
            }

            // Lucy XssFilter 인스턴스 생성
            xssFilter = XssFilter.getInstance(configFile);
            FILTER_CACHE.put(configFile, xssFilter);
            
        } catch (Exception e) {
            try {
                // 기본 설정으로 fallback
                if (!FILTER_CACHE.containsKey("default")) {
                    XssFilter defaultFilter = XssFilter.getInstance();
                    FILTER_CACHE.put("default", defaultFilter);
                }
                xssFilter = FILTER_CACHE.get("default");
            } catch (Exception ex) {
                // Lucy 초기화 실패 시 null로 설정
                xssFilter = null;
                System.err.println("Lucy XssFilter 초기화 실패: " + ex.getMessage());
            }
        }
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        return filterXssValue(value);
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }

        String[] filteredValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            filteredValues[i] = filterXssValue(values[i]);
        }
        return filteredValues;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return filterXssValue(value);
    }

    /**
     * XSS 필터링 처리
     */
    private String filterXssValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return value;
        }

        if (xssFilter != null) {
            try {
                // Lucy XSS Filter 적용
                return xssFilter.doFilter(value);
            } catch (Exception e) {
                // Lucy 필터 실패 시 기본 HTML 이스케이프
                System.err.println("Lucy XSS 필터링 실패: " + e.getMessage());
                return HtmlUtils.htmlEscape(value);
            }
        } else {
            // Lucy 필터가 없으면 기본 HTML 이스케이프
            return HtmlUtils.htmlEscape(value);
        }
    }
}