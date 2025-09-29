package com.petshop.community.dto;

/**
 * 게시글 검색 조건 DTO
 */
public class PostSearchRequest {
    
    private String categoryCode;        // 카테고리 코드
    private String keyword;             // 검색 키워드
    private String searchType = "all";  // 검색 타입 (title, content, author, all)
    private String statusCode = "PST001";  // 상태 코드 (기본값: 정상)
    private Boolean isNotice;           // 공지사항 여부
    private String sortBy = "created";  // 정렬 기준 (created, view, like)
    private String sortDir = "desc";    // 정렬 방향 (desc, asc)
    
    // 페이징을 위한 필드
    private int offset;                 // 시작 위치
    private int size = 20;              // 페이지 크기

    public PostSearchRequest() {}

    public String getCategoryCode() { return categoryCode; }
    public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }

    public String getKeyword() { return keyword != null ? keyword.trim() : null; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getSearchType() { return searchType; }
    public void setSearchType(String searchType) { this.searchType = searchType; }

    public String getStatusCode() { return statusCode; }
    public void setStatusCode(String statusCode) { this.statusCode = statusCode; }

    public Boolean getIsNotice() { return isNotice; }
    public void setIsNotice(Boolean isNotice) { this.isNotice = isNotice; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortDir() { return sortDir; }
    public void setSortDir(String sortDir) { this.sortDir = sortDir; }

    public int getOffset() { return offset; }
    public void setOffset(int offset) { this.offset = offset; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    
    // 검색 조건 존재 여부
    public boolean hasSearchCondition() {
        return keyword != null && !keyword.trim().isEmpty();
    }
}