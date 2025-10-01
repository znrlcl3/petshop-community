package com.petshop.community.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

/**
 * 회원 정보 DTO
 * MyBatis 매핑 및 계층 간 데이터 전송용
 */
public class MemberDto {
    
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    private String email;
    private String nickname;
    private String phone;
    private String profileImage;
    private String introduction;
    private String role;
    private String status;
    private boolean verified;
    private String verificationType;
    private LocalDateTime verifiedAt;
    private Long verifiedBy;
    private LocalDateTime lastLoginAt;
    private Integer loginCount;
    private Integer postCount;
    private Integer commentCount;
    private boolean deleted;
    private LocalDateTime deletedAt;
    private Long deletedBy;
    private String deleteReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String zipcode;
    private String address;
    private String detailAddress;

    public MemberDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public String getIntroduction() { return introduction; }
    public void setIntroduction(String introduction) { this.introduction = introduction; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public String getVerificationType() { return verificationType; }
    public void setVerificationType(String verificationType) { this.verificationType = verificationType; }

    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }

    public Long getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(Long verifiedBy) { this.verifiedBy = verifiedBy; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public Integer getLoginCount() { return loginCount; }
    public void setLoginCount(Integer loginCount) { this.loginCount = loginCount; }

    public Integer getPostCount() { return postCount; }
    public void setPostCount(Integer postCount) { this.postCount = postCount; }

    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public Long getDeletedBy() { return deletedBy; }
    public void setDeletedBy(Long deletedBy) { this.deletedBy = deletedBy; }

    public String getDeleteReason() { return deleteReason; }
    public void setDeleteReason(String deleteReason) { this.deleteReason = deleteReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getZipcode() {return zipcode;}
	public void setZipcode(String zipCode) {this.zipcode = zipCode;}

	public String getAddress() {return address;}
	public void setAddress(String address) {this.address = address;}

	public String getDetailAddress() {return detailAddress;}
	public void setDetailAddress(String detailAddress) {this.detailAddress = detailAddress;}

	public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
    
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    
    public boolean isSuspended() {
        return "SUSPENDED".equals(status);
    }
    
    public boolean isExpert() {
        return verified && "EXPERT".equals(verificationType);
    }
    
    public boolean isBusiness() {
        return verified && "BUSINESS".equals(verificationType);
    }
    
    public boolean isInfluencer() {
        return verified && "INFLUENCER".equals(verificationType);
    }
}