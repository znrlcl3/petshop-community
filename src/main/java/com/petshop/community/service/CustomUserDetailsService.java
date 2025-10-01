package com.petshop.community.service;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.petshop.community.dto.MemberDto;
import com.petshop.community.mapper.MemberMapper;
import com.petshop.community.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberMapper memberMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberDto member = memberMapper.findByUsername(username);
        
        if (member == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }
        
        return createUserDetails(member);
    }

    private UserDetails createUserDetails(MemberDto member) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        
        // 기본 권한 추가
        authorities.add(new SimpleGrantedAuthority("ROLE_" + member.getRole()));
        
        // 인증된 회원에게 추가 권한 부여
        if (member.isVerified()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_VERIFIED"));
            
            // 인증 타입별 추가 권한
            if (member.isExpert()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_EXPERT"));
            } else if (member.isBusiness()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_BUSINESS"));
            } else if (member.isInfluencer()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_INFLUENCER"));
            }
        }
        
        return new CustomUserDetails(
                member.getUsername(),
                member.getPassword(),
                member.isActive(),              // enabled
                true,                           // accountNonExpired
                true,                           // credentialsNonExpired
                !member.isSuspended(),          // accountNonLocked
                authorities,
                member.getId(),                 // memberId
                member.getNickname(),           // nickname
                member.isVerified(),            // verified
                member.getVerificationType()    // verificationType
            );
    }
}