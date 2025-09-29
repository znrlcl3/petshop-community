package com.petshop.community.mapper;

import com.petshop.community.dto.CodeDto;
import com.petshop.community.dto.PostDto;
import com.petshop.community.dto.PostSearchRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CodeMapper {
    
	/**
     * 그룹별 코드 목록 조회
     */
	List<CodeDto> selectCodesByGroupCode(String groupCode);
	/**
     * 코드명 조회
     */
	String selectCodeName(String groupCode, String code);
	/**
     * 코드의 확장값1 조회
     */
	String selectCodeExtraValue1(String groupCode, String code);
	/**
     * 코드의 확장값2 조회
     */
	String selectCodeExtraValue2(String groupCode, String code);
	/**
     * 코드 상세 정보 조회
     */
	CodeDto selectCode(String groupCode, String code);
    /**
     * 확장값1(URL경로)로 코드 조회 (역방향 조회)
     */
	String selectCodeByExtraValue1(String groupCode, String extraValue1);
    /**
     * 코드 존재 여부 확인
     */
	int existsCode(String groupCode, String code);
}