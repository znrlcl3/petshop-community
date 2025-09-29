package com.petshop.community.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.petshop.community.dto.CodeDto;
import com.petshop.community.mapper.CodeMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CodeService {
    
    private static final Logger log = LoggerFactory.getLogger(CodeService.class);
    private final CodeMapper codeMapper;
    
    /**
     * 그룹별 코드 목록 조회 (캐시 적용)
     */
    @Cacheable(value = "codes", key = "#groupCode")
    public List<CodeDto> getCodesByGroup(String groupCode) {
        log.debug("그룹별 코드 조회 - 그룹: {}", groupCode);
        return codeMapper.selectCodesByGroupCode(groupCode);
    }
    
    /**
     * 코드명 조회
     */
    @Cacheable(value = "codeName", key = "#groupCode + '_' + #code")
    public String getCodeName(String groupCode, String code) {
        log.debug("코드명 조회 - 그룹: {}, 코드: {}", groupCode, code);
        String codeName = codeMapper.selectCodeName(groupCode, code);
        return codeName != null ? codeName : code; // 코드를 찾을 수 없으면 코드 자체를 반환
    }
    
    /**
     * 코드의 확장값1 조회 (URL 경로 등에 사용)
     */
    @Cacheable(value = "codeExtraValue1", key = "#groupCode + '_' + #code")
    public String getCodeExtraValue1(String groupCode, String code) {
        log.debug("코드 확장값1 조회 - 그룹: {}, 코드: {}", groupCode, code);
        return codeMapper.selectCodeExtraValue1(groupCode, code);
    }
    
    /**
     * 코드의 확장값2 조회
     */
    @Cacheable(value = "codeExtraValue2", key = "#groupCode + '_' + #code")
    public String getCodeExtraValue2(String groupCode, String code) {
        log.debug("코드 확장값2 조회 - 그룹: {}, 코드: {}", groupCode, code);
        return codeMapper.selectCodeExtraValue2(groupCode, code);
    }
    
    /**
     * 코드 상세 정보 조회
     */
    @Cacheable(value = "codeDetail", key = "#groupCode + '_' + #code")
    public CodeDto getCode(String groupCode, String code) {
        log.debug("코드 상세 조회 - 그룹: {}, 코드: {}", groupCode, code);
        return codeMapper.selectCode(groupCode, code);
    }
    
    /**
     * 그룹별 코드를 Map으로 반환 (코드 -> 이름)
     */
    public Map<String, String> getCodesAsMap(String groupCode) {
        List<CodeDto> codes = getCodesByGroup(groupCode);
        return codes.stream()
                .collect(Collectors.toMap(
                    CodeDto::getCode, 
                    CodeDto::getName,
                    (existing, replacement) -> existing // 중복 키 처리
                ));
    }
    
    /**
     * 그룹별 코드를 Map으로 반환 (코드 -> 확장값1)
     */
    public Map<String, String> getCodesExtraValue1AsMap(String groupCode) {
        List<CodeDto> codes = getCodesByGroup(groupCode);
        return codes.stream()
                .filter(code -> code.getExtraValue1() != null)
                .collect(Collectors.toMap(
                    CodeDto::getCode,
                    CodeDto::getExtraValue1,
                    (existing, replacement) -> existing
                ));
    }
    
    /**
     * 확장값1(URL경로)로 코드 조회 (역방향 조회)
     */
    @Cacheable(value = "codeByExtraValue1", key = "#groupCode + '_' + #extraValue1")
    public String getCodeByExtraValue1(String groupCode, String extraValue1) {
        log.debug("확장값1로 코드 조회 - 그룹: {}, 확장값1: {}", groupCode, extraValue1);
        return codeMapper.selectCodeByExtraValue1(groupCode, extraValue1);
    }
    
    /**
     * 코드 존재 여부 확인
     */
    public boolean existsCode(String groupCode, String code) {
        return codeMapper.existsCode(groupCode, code) > 0;
    }
    
    @Cacheable(value = "categoryPathMap", key = "#groupCode")
    public Map<String, CategoryInfo> getCategoryPathMap(String groupCode) {
        List<CodeDto> codes = getCodesByGroup(groupCode);
        
        return codes.stream()
                .filter(code -> code.getExtraValue1() != null)
                .collect(Collectors.toMap(
                    CodeDto::getExtraValue1,  // key: "free", "qna"
                    code -> new CategoryInfo(code.getCode(), code.getName()),
                    (existing, replacement) -> existing
                ));
    }
    
    // 내부 클래스
    public static class CategoryInfo {
        private final String code;
        private final String name;
        
        public CategoryInfo(String code, String name) {
            this.code = code;
            this.name = name;
        }
        
        public String getCode() { return code; }
        public String getName() { return name; }
    }
}