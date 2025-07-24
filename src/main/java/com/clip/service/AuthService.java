package com.clip.service;

import com.clip.config.security.CustomUserDetails;
import com.clip.config.security.service.CustomUserDetailsService;
import com.clip.config.security.service.JwtService;
import com.clip.dto.LoginRequestDTO;
import com.clip.dto.LoginResponseDTO;
import com.clip.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request, HttpServletRequest httpRequest) {

        // 인증
        authenticateUser(request);

        // 유저 조회
        CustomUserDetails userDetails = (CustomUserDetails)getUserByUserId(request);
        User user = userDetails.getUser();

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        boolean isDev = isDevEnvironment(httpRequest);

        ResponseCookie accessCookie = createAccessTokenCookie(accessToken, isDev);
        ResponseCookie refreshCookie = createRefreshTokenCookie(refreshToken, isDev);

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

    private boolean isDevEnvironment(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        return origin != null && origin.contains("localhost");
    }

    private ResponseCookie createAccessTokenCookie(String accessToken, boolean isDev) {
        return ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(!isDev == true) // 운영환경이면 true, 개발이면 false
                .sameSite(isDev ? "Lax" : "None")
                .path("/")
                .maxAge(60 * 15)
                .build();
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken, boolean isDev) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(!isDev == true)
                .sameSite(isDev ? "Lax" : "None")
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
                        .nickname(user.getNickname())
                        .provider(user.getProvider())
                        .build())
                .build();
    }
}
