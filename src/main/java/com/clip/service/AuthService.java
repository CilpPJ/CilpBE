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

        ResponseCookie accessCookie = createAccessTokenCookie(accessToken, httpRequest);
        ResponseCookie refreshCookie = createRefreshTokenCookie(refreshToken, httpRequest);

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
        String host = request.getServerName();
        return host.contains("localhost") || host.contains("local");
    }

    private ResponseCookie createAccessTokenCookie(String accessToken, HttpServletRequest request) {
        boolean isDev = isDevEnvironment(request);
        String domain = request.getServerName(); // local.clipin.store or www.clipin.store

        return ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(!isDev)
                .sameSite(isDev ? "Lax" : "None")
                .path("/")
                .domain(domain) // 요청 도메인 기준으로 설정
                .maxAge(60 * 15)
                .build();
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken, HttpServletRequest request) {
        boolean isDev = isDevEnvironment(request);
        String domain = request.getServerName();

        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(!isDev)
                .sameSite(isDev ? "Lax" : "None")
                .path("/")
                .domain(domain)
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
