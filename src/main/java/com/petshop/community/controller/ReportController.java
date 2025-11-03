package com.petshop.community.controller;

import com.petshop.community.dto.ReportRequestDto;
import com.petshop.community.security.CustomUserDetails;
import com.petshop.community.service.ReportService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<?> submitReport(
            @Valid @RequestBody ReportRequestDto reportRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        if (bindingResult.hasErrors()) {
            log.warn("신고 접수 유효성 검증 실패: {}", bindingResult.getAllErrors());
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(errorMessage);
        }

        try {
            reportService.createReport(reportRequest, user.getMemberId());
            return ResponseEntity.ok().body("신고가 정상적으로 접수되었습니다.");

        } catch (IllegalArgumentException e) {
            log.warn("신고 접수 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // 409 Conflict
        } catch (Exception e) {
            log.error("신고 처리 중 알 수 없는 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("신고 처리 중 오류가 발생했습니다.");
        }
    }
}
