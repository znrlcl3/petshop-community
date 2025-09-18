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

        MemberDto member = new MemberDto();
        member.setUsername(signupDto.username());
        member.setPassword(passwordEncoder.encode(signupDto.password()));
        member.setEmail(signupDto.email());
        member.setNickname(signupDto.nickname());
        member.setPhone(signupDto.phone());
        member.setRole("USER");
        member.setStatus("ACTIVE");
        member.setVerified(false);
        member.setLoginCount(0);
        member.setPostCount(0);
        member.setCommentCount(0);
        member.setDeleted(false);

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