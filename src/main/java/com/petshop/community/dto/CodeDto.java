package com.petshop.community.dto;

import java.time.LocalDateTime;

/**
 * 코드 정보 DTO
 * MyBatis 매핑 및 계층 간 데이터 전송용
 */
public class CodeDto {
    
    private Long id;
    private Long groupId;
    private String code;
    private String name;
    private String description;
    private String extraValue1;
    private String extraValue2;
    private String extraValue3;
    private int displayOrder;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // JOIN으로 가져올 수 있는 추가 정보
    private String groupCode;
    private String groupName;

    public CodeDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getExtraValue1() { return extraValue1; }
    public void setExtraValue1(String extraValue1) { this.extraValue1 = extraValue1; }

    public String getExtraValue2() { return extraValue2; }
    public void setExtraValue2(String extraValue2) { this.extraValue2 = extraValue2; }

    public String getExtraValue3() { return extraValue3; }
    public void setExtraValue3(String extraValue3) { this.extraValue3 = extraValue3; }

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getGroupCode() { return groupCode; }
    public void setGroupCode(String groupCode) { this.groupCode = groupCode; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
}