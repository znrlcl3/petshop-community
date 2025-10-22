package com.petshop.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class PetshopCommunityApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        // WAR 배포 시 설정
        return application.sources(PetshopCommunityApplication.class);
    }

    public static void main(String[] args) {
        // JAR로 직접 실행 시
        SpringApplication.run(PetshopCommunityApplication.class, args);
    }
}