package com.petshop.community.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.petshop.community.dto.ReportAdminViewDto;
import com.petshop.community.dto.ReportDto;
import com.petshop.community.dto.ReportRequestDto;
import com.petshop.community.exception.ResourceNotFoundException;
import com.petshop.community.mapper.PostMapper;
import com.petshop.community.mapper.ReportMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportMapper reportMapper;
    private final PostMapper postMapper; 
    
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
    
    @Transactional
    @PreAuthorize("hasRole('ADMIN')") // 관리자 권한 체크
    public void processReport(Long reportId, String action, Long reportedPostId, Long processorId) {
        
        String statusCode;
        
        if ("HIDE_POST".equals(action)) {
            // 1. 게시글 소프트 삭제
            if (reportedPostId == null) {
                throw new IllegalArgumentException("게시글 숨기기 액션은 신고된 게시글 ID가 필수입니다.");
            }
            // deletePost는 삭제된 행의 수를 반환
            if (postMapper.deletePost(reportedPostId, processorId) == 0) {
                // 게시글이 이미 삭제되었거나 존재하지 않는 경우
                throw new ResourceNotFoundException("신고된 게시글 ID " + reportedPostId + "를 찾을 수 없습니다.");
            }
            // 2. 신고 상태를 '처리완료' (RPT003)로 변경
            statusCode = "RPT003"; 
            
        } else if ("RESOLVE".equals(action)) {
            // 신고 상태만 '반려됨' (RPT004)으로 변경
            statusCode = "RPT004"; 
            
        } else {
            throw new IllegalArgumentException("유효하지 않은 신고 처리 액션입니다: " + action);
        }

        // 3. 신고 상태 업데이트 (reports 테이블)
        if (reportMapper.updateReportStatus(reportId, statusCode, processorId) == 0) {
            // 신고가 이미 처리되었거나 존재하지 않는 경우
            throw new ResourceNotFoundException("신고 ID " + reportId + "가 이미 처리되었거나 존재하지 않습니다.");
        }
        
        log.info("신고 처리 완료. ID: {}, Action: {}", reportId, action);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    public List<ReportAdminViewDto> getReportList() {
        return reportMapper.selectReportList();
    }
}
