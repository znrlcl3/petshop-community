package com.petshop.community.service;

import com.petshop.community.dto.MemberDto;
import com.petshop.community.dto.SignupDto;
import com.petshop.community.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    public void registerMember(SignupDto signupDto) {
        // 중복 체크
        if (memberMapper.existsByUsername(signupDto.username())) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }
        
        if (memberMapper.existsByEmail(signupDto.email())) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupDto.password());

        // MemberDto 생성 (Record 생성자 사용)
        MemberDto member = new MemberDto(
            null, // id는 auto increment
            signupDto.username(),
            encodedPassword,
            signupDto.email(),
            signupDto.nickname(),
            signupDto.phone(),
            null, // profileImage
            "USER", // role
            "ACTIVE", // status
            false, // verified
            null, // verifiedAt
            null, // verifiedBy
            null, // lastLoginAt
            null, // createdAt (DB에서 자동 설정)
            null  // updatedAt (DB에서 자동 설정)
        );

        memberMapper.insertMember(member);
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return memberMapper.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return memberMapper.existsByEmail(email);
    }

    public void updateLastLoginTime(String username) {
        memberMapper.updateLastLoginTime(username);
    }
}