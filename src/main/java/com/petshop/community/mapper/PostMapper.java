package com.petshop.community.mapper;

import com.petshop.community.dto.PostDto;
import com.petshop.community.dto.PostSearchRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
    
    /**
     * 게시글 목록 조회
     */
    List<PostDto> selectPosts(PostSearchRequest searchRequest);
    
    /**
     * 게시글 전체 개수 조회
     */
    int selectPostsCount(PostSearchRequest searchRequest);
    
    /**
     * 게시글 상세 조회
     */
    PostDto selectPostById(@Param("id") Long id);
    
    /**
     * 게시글 등록
     */
    int insertPost(PostDto post);
    
    /**
     * 게시글 수정
     */
    int updatePost(PostDto post);
    
    /**
     * 게시글 삭제 (소프트 삭제)
     */
    int deletePost(@Param("id") Long id, @Param("deletedBy") Long deletedBy);
    
    /**
     * 조회수 증가
     */
    int increaseViewCount(@Param("id") Long id);
    
    /**
     * 좋아요 수 증가
     */
    int increaseLikeCount(@Param("id") Long id);
    
    /**
     * 좋아요 수 감소
     */
    int decreaseLikeCount(@Param("id") Long id);
    
    /**
     * 댓글 수 증가
     */
    int increaseCommentCount(@Param("id") Long id);
    
    /**
     * 댓글 수 감소
     */
    int decreaseCommentCount(@Param("id") Long id);
}