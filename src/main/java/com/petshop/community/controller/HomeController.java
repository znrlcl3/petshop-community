package com.petshop.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home(Model model) {
    	
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("username", auth.getName());
            model.addAttribute("authorities", auth.getAuthorities());
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        
        model.addAttribute("title", "펫샵 커뮤니티");
        return "home";
    }
    
    @GetMapping("/error/403")
    public String accessDenied() {
        return "error/403";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("title", "관리자 대시보드");
        return "admin/dashboard";
    }

    @GetMapping("/member/profile")
    public String memberProfile(Model model) {

    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        model.addAttribute("authorities", auth.getAuthorities());
        model.addAttribute("title", "내 프로필");
        return "member/profile";
    }
}