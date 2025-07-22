package com.clip.config.security.service;

import com.clip.config.exception.CustomException;
import com.clip.config.security.CustomUserDetails;
import com.clip.entity.User;
import com.clip.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// UserDetailsService를 구현해서 Spring Security가 userId로 유저를 조회할 수 있도록 해줌

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        // userId 기준으로 DB에서 유저를 찾고, 없으면 예외 발생
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND" , userId));

        return new CustomUserDetails(user); // 우리가 만든 클래스
    }
}
