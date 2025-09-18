package com.petshop.community.service;

import com.petshop.community.dto.MemberDto;
import com.petshop.community.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

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
        authorities.add(new SimpleGrantedAuthority("ROLE_" + member.role()));
        
        // 검증된 회원에게 추가 권한 부여 (Record는 verified() 메서드 사용)
        if (member.verified()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_VERIFIED"));
        }
        
        return User.builder()
                .username(member.username())
                .password(member.password())
                .disabled(!member.status().equals("ACTIVE"))
                .accountLocked(member.status().equals("SUSPENDED"))
                .authorities(authorities)
                .build();
    }
}