package com.petshop.community.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.petshop.community.dto.ReportAdminViewDto;
import com.petshop.community.exception.ResourceNotFoundException;
import com.petshop.community.security.CustomUserDetails;
import com.petshop.community.service.ReportService;

/**
 * 관리자 페이지 관련 요청을 처리하는 컨트롤러입니다.
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ReportService reportService;

    public AdminController(ReportService reportService) {
        this.reportService = reportService;
    }


    /**
     * 관리자 대시보드 페이지
     */
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("title", "관리자 대시보드");
        return "admin/dashboard";
    }

    /**
     * 신고 목록 페이지
     */
    @GetMapping("/reports")
    public String adminReportList(Model model) {
    	List<ReportAdminViewDto> reportList = reportService.getReportList(); 
        model.addAttribute("title", "신고 내역 목록");
        model.addAttribute("reports", reportList);
        return "admin/report_list";
    }
    
    /**
     * 신고 처리 API (PATCH /admin/api/reports/{reportId}/process)
     */
    @PatchMapping("/api/reports/{reportId}/process")
    @ResponseBody
    public ResponseEntity<String> processReport(@PathVariable Long reportId, @RequestParam("action") String action, @RequestParam(value = "postId", required = false) Long reportedPostId,@AuthenticationPrincipal CustomUserDetails adminUser ) {
    	
    	if (adminUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("관리자 로그인이 필요합니다.");
        }
        Long processorId = adminUser.getMemberId(); 
    	
    	try {
            reportService.processReport(reportId, action, reportedPostId, processorId);
            return ResponseEntity.ok().body("신고 처리가 완료되었습니다.");
            
        } catch (IllegalArgumentException | ResourceNotFoundException e) { 
            return ResponseEntity.badRequest().body("처리 실패: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("신고 처리 중 오류 발생: " + e.getMessage());
            return ResponseEntity.internalServerError().body("신고 처리 중 서버 오류가 발생했습니다.");
        }
    }

}