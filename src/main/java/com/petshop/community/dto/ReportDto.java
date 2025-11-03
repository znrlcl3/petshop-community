package com.petshop.community.dto;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 신고 내역 DTO
 * MyBatis 매핑 및 DB 데이터 전송용
 */
@Data
public class ReportDto {

    private Long id;                // 신고 ID (PK)
    private Long reporterId;        // 신고한 회원 ID
    private String targetType;      // 신고 대상 타입 (Enum.name()으로 저장)
    private Long targetId;          // 신고 대상 ID
    private String reportReasonCode;  // 신고 사유 코드
    private String reportDetail;      // 신고 상세 내용
    private String statusCode;      // 처리 상태 코드
    private LocalDateTime processedAt;  // 처리 일시
    private Long processedBy;       // 처리한 관리자 ID
    private String processorComment;  // 처리자 코멘트
    private LocalDateTime createdAt;    // 신고 접수 일시

    // 기본 생성자
    public ReportDto() {
    }
}
