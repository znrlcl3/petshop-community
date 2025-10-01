package com.petshop.community.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.petshop.community.dto.PostCreateDto;
import com.petshop.community.dto.PostDto;
import com.petshop.community.dto.PostEditDto;
import com.petshop.community.dto.PostSearchRequest;
import com.petshop.community.mapper.PostMapper;
import com.petshop.community.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    public Long createPost(PostCreateDto dto, String categoryCode, CustomUserDetails user, String clientIp) {
        
        // DTO -> Entity 변환
        PostDto post = new PostDto();
        post.setCategoryCode(categoryCode);
        post.setTitle(dto.getTitle().trim());
        post.setContent(dto.getContent().trim());
        post.setStatusCode("PST001"); // 정상
        post.setMemberId(user.getMemberId());
        post.setAuthorNickname(user.getNickname());
        post.setAuthorVerified(user.isVerified());
        post.setWriterIp(clientIp);
        
        // 게시글 등록
        postMapper.insertPost(post);
        
        return post.getId();
    }
    
    @Transactional
    public void updatePost(Long postId, PostEditDto dto, CustomUserDetails user, String clientIp) {
        
        // 기존 게시글 조회
        PostDto existingPost = postMapper.selectPostById(postId);
        
        if (existingPost == null || existingPost.isDeleted()) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }
        
        // 권한 검증
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
        
        if (!existingPost.getAuthorNickname().equals(user.getNickname()) && !isAdmin) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }
        
        // 변경 여부 확인
        if (existingPost.getTitle().equals(dto.getTitle()) && 
            existingPost.getContent().equals(dto.getContent())) {
            throw new IllegalArgumentException("변경된 내용이 없습니다.");
        }
        
        // 업데이트
        PostDto updatePost = new PostDto();
        updatePost.setId(postId);
        updatePost.setTitle(dto.getTitle().trim());
        updatePost.setContent(dto.getContent().trim());
        
        int result = postMapper.updatePost(updatePost);
        if (result != 1) {
            throw new RuntimeException("게시글 수정에 실패했습니다.");
        }
    }
}