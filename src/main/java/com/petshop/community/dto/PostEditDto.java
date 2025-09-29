package com.petshop.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PostEditDto {
    
    @NotBlank(message = "제목은 필수입니다")
    @Size(min = 2, max = 200, message = "제목은 2자 이상 200자 이하여야 합니다")
    private String title;
    
    @NotBlank(message = "내용은 필수입니다")
    @Size(min = 10, max = 10000, message = "내용은 10자 이상 10000자 이하여야 합니다")
    private String content;
    
    public PostEditDto() {}
    
    public PostEditDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
    
    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}