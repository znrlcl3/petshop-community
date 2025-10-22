package com.petshop.community.service;

import com.petshop.community.dto.LikeResponseDto;
import com.petshop.community.mapper.PostLikeMapper; // Mapper 추가 필요
import com.petshop.community.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeMapper postLikeMapper;
    private final PostMapper postMapper;

    /**
     * 게시글 좋아요 토글 처리
     */
    @Transactional
    public LikeResponseDto toggleLike(Long postId, Long memberId) {
        boolean exists = postLikeMapper.existsByPostIdAndMemberId(postId, memberId);
        boolean userLiked;

        if (exists) {
            // 이미 좋아요 -> 좋아요 취소
            postLikeMapper.delete(postId, memberId);
            postMapper.decrementLikeCount(postId);
            userLiked = false;
        } else {
            // 좋아요 안 함 -> 좋아요 처리
            postLikeMapper.insert(postId, memberId);
            postMapper.incrementLikeCount(postId);
            userLiked = true;
        }

        // 최신 좋아요 수 조회
        int newLikeCount = postMapper.getLikeCount(postId);

        return new LikeResponseDto(newLikeCount, userLiked);
    }

    /**
     * 사용자가 해당 게시글에 좋아요를 눌렀는지 확인
     */
    @Transactional(readOnly = true)
    public boolean hasUserLikedPost(Long postId, Long memberId) {
        return postLikeMapper.existsByPostIdAndMemberId(postId, memberId);
    }
}
