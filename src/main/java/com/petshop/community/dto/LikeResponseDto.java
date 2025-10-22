package com.petshop.community.dto;

/**
 * 좋아요 API 응답 DTO
 * @param newLikeCount 변경 후의 총 좋아요 수
 * @param userLiked 현재 사용자의 좋아요 상태 (true: 좋아요 누름, false: 취소)
 */
public record LikeResponseDto(int newLikeCount, boolean userLiked) {}