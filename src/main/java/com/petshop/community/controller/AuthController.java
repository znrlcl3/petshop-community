package com.petshop.community.controller;

import com.petshop.community.dto.SignupDto;
import com.petshop.community.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                          Model model) {
        
        // 이미 로그인된 사용자는 홈으로 리다이렉트
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
    	    return "redirect:/";
    	}
        
        if (error != null) {
            model.addAttribute("error", true);
        }
        
        return "auth/login";
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("signupDto", new SignupDto("", "", "", "", ""));
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("signupDto") SignupDto signupDto,
                        BindingResult bindingResult,
                        RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            return "auth/signup";
        }

        try {
            memberService.registerMember(signupDto);
            redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            bindingResult.reject("signupFailed", e.getMessage());
            return "auth/signup";
        }
    }

    // AJAX 중복 체크
    @PostMapping("/check/username")
    @ResponseBody
    public boolean checkUsername(@RequestParam String username) {
        return !memberService.existsByUsername(username);
    }

    @PostMapping("/check/email") 
    @ResponseBody
    public boolean checkEmail(@RequestParam String email) {
        return !memberService.existsByEmail(email);
    }
}