package com.petshop.community.service;

import com.petshop.community.dto.ReportDto;
import com.petshop.community.dto.ReportRequestDto;
import com.petshop.community.mapper.ReportMapper; // Mapper 추가 필요
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportMapper reportMapper;

    @Transactional
    public void createReport(ReportRequestDto dto, Long reporterId) {
        String targetType = dto.getTargetType().name();
        Long targetId = dto.getTargetId();

        // 1. 동일 사용자가 동일 대상에 대해 이미 신고했는지 확인
        boolean alreadyReported = reportMapper.existsByReporterAndTarget(reporterId, targetType, targetId);

        if (alreadyReported) {
            log.warn("중복 신고 시도: reporterId={}, targetType={}, targetId={}", reporterId, targetType, targetId);
            throw new IllegalArgumentException("이미 신고한 대상입니다.");
        }

        // 2. ReportDto
        ReportDto report = new ReportDto();
        report.setReporterId(reporterId);
        report.setTargetType(targetType);
        report.setTargetId(targetId);
        report.setReportReasonCode(dto.getReportReasonCode());
        report.setReportDetail(dto.getReportDetail());
        report.setStatusCode("RPT001"); // '접수됨' 상태

        // 3. DB에 저장
        reportMapper.insertReport(report);
        log.info("신고 접수 완료: reportId={}", report.getId());
    }
}
