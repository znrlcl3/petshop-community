package com.petshop.community.controller;

import com.petshop.community.util.XssUtils;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

    @GetMapping("/test")
    public String testPage() {
        return """
            
            사용법 :
            1. GET 즉시 테스트: /test/simple?input=<script>alert('test')</script>
            2. POST JSON 테스트: /test/xss (JSON 형태)
            3. POST 폼 테스트: /test/form?input=<script>alert('test')</script>
            
            빠른 테스트 링크:
            - /test/simple?input=<script>alert('XSS')</script>
            - /test/simple?input=<img src="x" onerror="alert('XSS')">
            - /test/simple?input=<p><strong>안전한</strong> HTML</p>
            
            Lucy XSS Filter가 정상 작동 중입니다!
            """;
    }

    @PostMapping("/test/xss")
    public TestResult testXss(@RequestParam("input") String input) {
        System.out.println("=== POST XSS 테스트 ===");
        System.out.println("원본 입력: " + input);
        
        String filtered = XssUtils.filterHtml(input);
        String strict = XssUtils.filterStrict(input);
        String escaped = XssUtils.escapeHtml(input);
        
        System.out.println("Lucy 필터링: " + filtered);
        System.out.println("엄격 필터링: " + strict);
        System.out.println("HTML 이스케이프: " + escaped);
        
        // 필터링 결과 분석
        boolean xssDetected = !input.equals(filtered);
        System.out.println("XSS 공격 감지: " + (xssDetected ? "YES" : "NO"));
        System.out.println("========================");
        
        return new TestResult(input, filtered, strict, escaped, xssDetected);
    }
    
    @GetMapping("/test/simple")
    public String simpleTest(@RequestParam("input") String input) {
        System.out.println("=== GET 간단 테스트 ===");
        System.out.println("원본: " + input);
        
        String result = XssUtils.filterHtml(input);
        System.out.println("필터링 후: " + result);
        
        boolean isFiltered = !input.equals(result);
        System.out.println("필터링 적용: " + (isFiltered ? "YES" : "NO"));
        System.out.println("========================");
        
        return String.format("""
            🛡️ XSS 필터링 결과:
            
            📥 원본 입력: %s
            📤 필터링 결과: %s
            🔍 필터링 적용: %s
            
            %s
            """, 
            input, 
            result.isEmpty() ? "(빈 값 - 완전 차단됨)" : result,
            isFiltered ? "✅ YES (XSS 차단됨)" : "ℹ️ NO (안전한 입력)",
            isFiltered ? "🚨 XSS 공격이 감지되어 필터링되었습니다!" : "✅ 안전한 입력입니다."
        );
    }

    @PostMapping("/test/form")
    public TestResult testForm(@RequestParam("input") String input) {
        return testXss(input); // POST 테스트와 동일
    }

    @GetMapping("/test/batch")
    public String batchTest() {
        System.out.println("=== 배치 XSS 테스트 시작 ===");
        
        String[] testCases = {
            "<script>alert('XSS')</script>",
            "<img src=\"x\" onerror=\"alert('XSS')\">",
            "<a href=\"javascript:alert('XSS')\">링크</a>",
            "<div onmouseover=\"alert('XSS')\">마우스오버</div>",
            "<p><strong>안전한</strong> HTML <em>태그</em></p>",
            "<a href=\"https://example.com\">안전한 링크</a>",
            "일반 텍스트 입력"
        };
        
        StringBuilder result = new StringBuilder("🧪 배치 XSS 테스트 결과:\n\n");
        
        for (int i = 0; i < testCases.length; i++) {
            String input = testCases[i];
            String filtered = XssUtils.filterHtml(input);
            boolean isFiltered = !input.equals(filtered);
            
            result.append(String.format("""
                테스트 %d:
                📥 입력: %s
                📤 결과: %s
                🔍 상태: %s
                
                """, 
                i + 1,
                input,
                filtered.isEmpty() ? "(완전 차단됨)" : filtered,
                isFiltered ? "⚠️ 필터링됨" : "✅ 안전함"
            ));
            
            System.out.println("테스트 " + (i + 1) + " - 입력: " + input + " → 결과: " + filtered);
        }
        
        System.out.println("=== 배치 XSS 테스트 완료 ===");
        return result.toString();
    }

    @GetMapping("/test/status")
    public TestStatus getFilterStatus() {
        try {
            // Lucy 필터 상태 확인
            String testInput = "<script>alert('test')</script>";
            String filtered = XssUtils.filterHtml(testInput);
            boolean isWorking = !testInput.equals(filtered);
            
            return new TestStatus(
                true,
                isWorking,
                "Lucy XSS Filter",
                isWorking ? "정상 작동 중" : "필터링 미적용",
                testInput,
                filtered
            );
        } catch (Exception e) {
            return new TestStatus(
                false,
                false,
                "Lucy XSS Filter",
                "오류: " + e.getMessage(),
                null,
                null
            );
        }
    }

    // JSON 응답용 클래스들
    public static class TestResult {
        private String original;
        private String filtered;
        private String strict;
        private String escaped;
        private boolean xssDetected;
        
        public TestResult(String original, String filtered, String strict, String escaped, boolean xssDetected) {
            this.original = original;
            this.filtered = filtered;
            this.strict = strict;
            this.escaped = escaped;
            this.xssDetected = xssDetected;
        }
        
        // Getters
        public String getOriginal() { return original; }
        public String getFiltered() { return filtered; }
        public String getStrict() { return strict; }
        public String getEscaped() { return escaped; }
        public boolean isXssDetected() { return xssDetected; }
    }
    
    public static class TestStatus {
        private boolean available;
        private boolean working;
        private String filterType;
        private String status;
        private String testInput;
        private String testOutput;
        
        public TestStatus(boolean available, boolean working, String filterType, String status, String testInput, String testOutput) {
            this.available = available;
            this.working = working;
            this.filterType = filterType;
            this.status = status;
            this.testInput = testInput;
            this.testOutput = testOutput;
        }
        
        // Getters
        public boolean isAvailable() { return available; }
        public boolean isWorking() { return working; }
        public String getFilterType() { return filterType; }
        public String getStatus() { return status; }
        public String getTestInput() { return testInput; }
        public String getTestOutput() { return testOutput; }
    }
}