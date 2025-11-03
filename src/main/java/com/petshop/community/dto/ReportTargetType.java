package com.petshop.community.dto;

/**
 * 신고 대상 타입을 정의하는 Enum
 */
public enum ReportTargetType {
    POST,          // 일반 게시글
    COMMENT,       // 일반 댓글
    TRADE_POST,    // 중고거래 게시글
    TRADE_COMMENT  // 중고거래 댓글
}
