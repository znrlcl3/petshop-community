package com.petshop.community.dto;

import java.time.LocalDateTime;

/**
 * 게시글 정보 DTO
 * MyBatis 매핑 및 계층 간 데이터 전송용
 */
public class PostDto {
    
    private Long id;
    private String categoryCode;
    private Long memberId;
    private String title;
    private String content;
    private int viewCount;
    private int likeCount;
    private int commentCount;
    private String statusCode;
    private boolean isNotice;
    private boolean isTop;
    private String authorNickname;
    private boolean authorVerified;
    private String writerIp;
    private boolean isDeleted;
    private LocalDateTime deletedAt;
    private Long deletedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 추가 정보 (JOIN으로 가져올 수 있는 데이터)
    private String categoryName;
    private String statusName;
    private boolean hasAttachments;

    public PostDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCategoryCode() { return categoryCode; }
    public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }

    public String getStatusCode() { return statusCode; }
    public void setStatusCode(String statusCode) { this.statusCode = statusCode; }

    public boolean isNotice() { return isNotice; }
    public void setNotice(boolean notice) { this.isNotice = notice; }

    public boolean isTop() { return isTop; }
    public void setTop(boolean top) { this.isTop = top; }

    public String getAuthorNickname() { return authorNickname; }
    public void setAuthorNickname(String authorNickname) { this.authorNickname = authorNickname; }

    public boolean isAuthorVerified() { return authorVerified; }
    public void setAuthorVerified(boolean authorVerified) { this.authorVerified = authorVerified; }

    public String getWriterIp() { return writerIp; }
    public void setWriterIp(String writerIp) { this.writerIp = writerIp; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { this.isDeleted = deleted; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public Long getDeletedBy() { return deletedBy; }
    public void setDeletedBy(Long deletedBy) { this.deletedBy = deletedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    public boolean isHasAttachments() { return hasAttachments; }
    public void setHasAttachments(boolean hasAttachments) { this.hasAttachments = hasAttachments; }
    
    // 시간 표시를 위한 헬퍼 메소드
    public String getTimeDisplay() {
        if (createdAt == null) return "";
        
        LocalDateTime now = LocalDateTime.now();
        long hoursDiff = java.time.Duration.between(createdAt, now).toHours();
        
        if (hoursDiff < 24) {
            return createdAt.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        } else {
            return createdAt.format(java.time.format.DateTimeFormatter.ofPattern("MM-dd"));
        }
    }
    
    // 제목 길이 제한
    public String getShortTitle(int maxLength) {
        if (title == null) return "";
        if (title.length() <= maxLength) {
            return title;
        }
        return title.substring(0, maxLength) + "...";
    }
    
    // 상태 확인 메소드들
    public boolean isActive() {
        return "PST001".equals(statusCode);
    }
    
    public boolean isHidden() {
        return "PST002".equals(statusCode);
    }
}