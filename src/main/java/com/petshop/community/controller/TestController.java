package com.petshop.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/test/password")
    public String testPassword(@RequestParam String password) {
        String hash = passwordEncoder.encode(password);
        System.out.println("비밀번호: " + password);
        System.out.println("해시: " + hash);
        
        boolean matches = passwordEncoder.matches(password, hash);
        System.out.println("매칭 결과: " + matches);
        
        return "해시: " + hash;
    }
}