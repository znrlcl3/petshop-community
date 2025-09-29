package com.petshop.community.service;

import com.petshop.community.dto.PostDto;
import com.petshop.community.dto.PostSearchRequest;
import com.petshop.community.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PostService {
    
    private final PostMapper postMapper;
    
    /**
     * 게시글 목록 조회
     */
    public Page<PostDto> getPosts(PostSearchRequest searchRequest, Pageable pageable) {
        log.info("게시글 목록 조회 - 카테고리: {}, 검색어: {}", 
                searchRequest.getCategoryCode(), searchRequest.getKeyword());
        
        // 페이징 정보 설정
        searchRequest.setOffset((int) pageable.getOffset());
        searchRequest.setSize(pageable.getPageSize());
        
        // 게시글 목록과 전체 개수 조회
        List<PostDto> posts = postMapper.selectPosts(searchRequest);
        int totalCount = postMapper.selectPostsCount(searchRequest);
        
        return new PageImpl<>(posts, pageable, totalCount);
    }
    
    @Transactional
    public PostDto getPostDetail(Long postId, String clientIp) {
        log.info("게시글 상세 조회 - ID: {}, IP: {}", postId, clientIp);
        
        PostDto post = postMapper.selectPostById(postId);
        if (post != null && !post.isDeleted()) {
            // 조회수 증가
            postMapper.increaseViewCount(postId);
            post.setViewCount(post.getViewCount() + 1);
        }
        return post;
    }
    
    @Transactional
    public void updatePost(PostDto post, String updatedBy) {
        if (post.getId() == null) {
            throw new IllegalArgumentException("게시글 ID가 필요합니다.");
        }
        
        // 기존 게시글 존재 여부 확인
        PostDto existingPost = postMapper.selectPostById(post.getId());
        if (existingPost == null || existingPost.isDeleted()) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }
        
        log.info("게시글 업데이트 - ID: {}, updatedBy: {}", post.getId(), updatedBy);
        
        int result = postMapper.updatePost(post);
        if (result != 1) {
            throw new RuntimeException("게시글 수정에 실패했습니다.");
        }
    }
}