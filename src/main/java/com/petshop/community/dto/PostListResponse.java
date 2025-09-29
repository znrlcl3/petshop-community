package com.petshop.community.dto;


import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostListResponse {
    
    private Long id;
    private String categoryCode;
    private String categoryName;
    private String title;
    private String authorNickname;
    private boolean authorVerified;
    private int viewCount;
    private int likeCount;
    private int commentCount;
    private String statusCode;
    private String statusName;
    private boolean isNotice;
    private boolean isTop;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean hasAttachments;  // 첨부파일 존재 여부
    
    // 시간 표시를 위한 헬퍼 메소드
    public String getTimeDisplay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime postTime = this.createdAt;
        
        long hoursDiff = java.time.Duration.between(postTime, now).toHours();
        
        if (hoursDiff < 24) {
            return postTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        } else {
            return postTime.format(java.time.format.DateTimeFormatter.ofPattern("MM-dd"));
        }
    }
    
    // 제목 길이 제한
    public String getShortTitle(int maxLength) {
        if (title.length() <= maxLength) {
            return title;
        }
        return title.substring(0, maxLength) + "...";
    }
}