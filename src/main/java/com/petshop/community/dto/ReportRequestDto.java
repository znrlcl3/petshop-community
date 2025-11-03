package com.petshop.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReportRequestDto {

    @NotNull(message = "신고 대상 타입은 필수입니다.")
    private ReportTargetType targetType; // 신고 대상 (POST, COMMENT 등)

    @NotNull(message = "신고 대상 ID는 필수입니다.")
    private Long targetId; // 신고 대상 ID (게시글/댓글 ID)

    @NotBlank(message = "신고 사유는 필수입니다.")
    private String reportReasonCode; // 신고 사유 코드 (codes 테이블)

    @Size(max = 1000, message = "상세 내용은 1000자를 넘을 수 없습니다.")
    private String reportDetail; // 신고 상세 내용 (선택)
}
