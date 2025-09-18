package com.petshop.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("title", "펫샵 커뮤니티");
        return "home";
    }
    
    @GetMapping("/error/403")
    public String accessDenied() {
        return "error/403";
    }
}