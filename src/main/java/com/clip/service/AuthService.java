package com.clip.service;

import com.clip.config.security.CustomUserDetails;
import com.clip.config.security.service.CustomUserDetailsService;
import com.clip.config.security.service.JwtService;
import com.clip.dto.LoginRequestDTO;
import com.clip.dto.LoginResponseDTO;
import com.clip.entity.User;
import com.clip.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {

        // 인증
        authenticateUser(request);

        // 유저 조회
        CustomUserDetails userDetails = (CustomUserDetails)getUserByUserId(request);
        User user = userDetails.getUser();

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        ResponseCookie accessCookie = createAccessTokenCookie(accessToken);
        ResponseCookie refreshCookie = createRefreshTokenCookie(refreshToken);

        // 응답 생성
        return buildLoginResponse(user, accessCookie, refreshCookie);
    }

    private void authenticateUser(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserId(), request.getPassword()
                )
        );
    }

    private UserDetails getUserByUserId(LoginRequestDTO request) {
        return userDetailsService.loadUserByUsername(request.getUserId());
    }

    private ResponseCookie createAccessTokenCookie(String accessToken) {
        return ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 15)
                .build();
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60 * 24 * 7)
                .build();
    }

    private LoginResponseDTO buildLoginResponse(User user, ResponseCookie accessCookie, ResponseCookie refreshCookie) {
        return LoginResponseDTO.builder()
                .accessCookie(accessCookie)
                .refreshCookie(refreshCookie)
                .loginData(LoginResponseDTO.LoginData.builder()
                        .message("로그인 성공")
                        .nickName(user.getNickName())
                        .provider(user.getProvider())
                        .build())
                .build();
    }

}
