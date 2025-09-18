package com.petshop.community.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

public record MemberDto(
    Long id,
    String username,
    @JsonIgnore String password,
    String email,
    String nickname,
    String phone,
    String profileImage,
    String role,
    String status,
    boolean verified,
    LocalDateTime verifiedAt,
    Long verifiedBy,
    LocalDateTime lastLoginAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    // 편의 메서드들
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
    
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    
    public boolean isSuspended() {
        return "SUSPENDED".equals(status);
    }
}