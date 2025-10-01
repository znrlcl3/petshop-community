package com.petshop.community.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Spring Security UserDetails 확장
 * 회원 정보(memberId, nickname 등)를 포함
 */
public class CustomUserDetails extends User {
    
    private final Long memberId;
    private final String nickname;
    private final boolean verified;
    private final String verificationType;
    
    public CustomUserDetails(String username, 
                           String password, 
                           Collection<? extends GrantedAuthority> authorities,
                           Long memberId,
                           String nickname,
                           boolean verified,
                           String verificationType) {
        super(username, password, authorities);
        this.memberId = memberId;
        this.nickname = nickname;
        this.verified = verified;
        this.verificationType = verificationType;
    }
    
    public CustomUserDetails(String username, 
                           String password, 
                           boolean enabled, 
                           boolean accountNonExpired,
                           boolean credentialsNonExpired, 
                           boolean accountNonLocked,
                           Collection<? extends GrantedAuthority> authorities,
                           Long memberId,
                           String nickname,
                           boolean verified,
                           String verificationType) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.memberId = memberId;
        this.nickname = nickname;
        this.verified = verified;
        this.verificationType = verificationType;
    }
    
    public Long getMemberId() {
        return memberId;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public boolean isVerified() {
        return verified;
    }
    
    public String getVerificationType() {
        return verificationType;
    }
    
    public boolean isExpert() {
        return verified && "EXPERT".equals(verificationType);
    }
    
    public boolean isBusiness() {
        return verified && "BUSINESS".equals(verificationType);
    }
    
    public boolean isInfluencer() {
        return verified && "INFLUENCER".equals(verificationType);
    }
}