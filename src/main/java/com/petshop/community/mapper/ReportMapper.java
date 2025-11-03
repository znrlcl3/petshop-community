package com.petshop.community.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.petshop.community.dto.ReportDto;

@Mapper
public interface ReportMapper {

    /**
     * 신고 내역을 DB에 삽입
     * @param report (id는 useGeneratedKeys로 DTO에 다시 채워짐)
     */
    void insertReport(ReportDto report);

    /**
     * 중복 신고 확인
     * @return 신고 내역이 있으면 true
     */
    boolean existsByReporterAndTarget(
            @Param("reporterId") Long reporterId,
            @Param("targetType") String targetType,
            @Param("targetId") Long targetId
    );
}
