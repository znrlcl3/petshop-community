package com.petshop.community.dto;

/**
 * 관리자 페이지 신고 목록 테이블에 데이터를 표시하기 위한 DTO입니다.
 */
public class ReportAdminViewDto {
    private Long reportId;
    private String reporterName;
    private String reportedPostTitle;
    private Long reportedPostId;
    private String reasonCode;
    private String reportedAt;
    private String statusCode;

    public ReportAdminViewDto(Long reportId, String reporterName, String reportedPostTitle, 
                              Long reportedPostId, String reasonCode, String reportedAt, String statusCode) {
        this.reportId = reportId;
        this.reporterName = reporterName;
        this.reportedPostTitle = reportedPostTitle;
        this.reportedPostId = reportedPostId;
        this.reasonCode = reasonCode;
        this.reportedAt = reportedAt;
        this.statusCode = statusCode;
    }

    // Getters
    public Long getReportId() { return reportId; }
    public String getReporterName() { return reporterName; }
    public String getReportedPostTitle() { return reportedPostTitle; }
    public Long getReportedPostId() { return reportedPostId; }
    public String getReasonCode() { return reasonCode; }
    public String getReportedAt() { return reportedAt; }
    public String getStatusCode() { return statusCode; }
    
    // 헬퍼 메서드: 미처리 상태인지 확인 (RPT001이 미처리 코드라고 가정)
    public boolean isUnprocessed() { 
        return "RPT001".equals(statusCode); 
    }

    public boolean isProcessed() {
        return !"RPT001".equals(statusCode);
    }
}
