package com.petshop.community.mapper;

import com.petshop.community.dto.MemberDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberMapper {
    
    MemberDto findByUsername(@Param("username") String username);
    
    MemberDto findByEmail(@Param("email") String email);
    
    MemberDto findById(@Param("id") Long id);
    
    int insertMember(MemberDto member);
    
    int updateMember(MemberDto member);
    
    int updateLastLoginTime(@Param("username") String username);
    
    boolean existsByUsername(@Param("username") String username);
    
    boolean existsByEmail(@Param("email") String email);
}