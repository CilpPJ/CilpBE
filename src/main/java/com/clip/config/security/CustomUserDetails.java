package com.clip.config.security;

// 우리가 만든 User 엔티티를 Spring Security가 이해할 수 있도록 감싸주는 어댑터 역할.

import com.clip.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final User user;  // 우리가 만든 User 엔티티 감싸기

    // 권한 목록 → 지금은 비워도 됨, 나중에 ROLE_USER 추가 가능
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한이 없다면 빈 리스트 or 기본 ROLE_USER 권장
        return Collections.emptyList();  // or List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return user.getPassword();  // 암호화된 비밀번호
    }

    @Override
    public String getUsername() {
        return user.getUserId();  // 로그인 식별자 (userId 필드)
    }

    public String getNickName() { return user.getNickName(); }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // 계정 만료 여부 (필요 시 DB 필드 추가해서 관리)
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // 계정 잠금 여부
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // 비밀번호 만료 여부
    }

    @Override
    public boolean isEnabled() {
        return true;  // 계정 활성화 여부
    }

    // 혹시 User 엔티티 직접 접근하고 싶을 때를 대비해 getter 제공
    public User getUser() {
        return user;
    }
}
