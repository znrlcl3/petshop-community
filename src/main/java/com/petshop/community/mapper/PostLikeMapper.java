package com.petshop.community.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PostLikeMapper {
    boolean existsByPostIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);
    void insert(@Param("postId") Long postId, @Param("memberId") Long memberId);
    void delete(@Param("postId") Long postId, @Param("memberId") Long memberId);
    
    
}

