package com.clip.controller;

import com.clip.config.security.CustomUserDetailsService;
import com.clip.config.security.JwtService;
import com.clip.dto.LoginRequestDTO;
import com.clip.dto.LoginResponseDTO;
import com.clip.dto.SignUpRequestDTO;
import com.clip.dto.SignUpResponseDTO;
import com.clip.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserService userService;

    @Operation(
            summary = "로그인",
            description = "아이디와 비밀번호를 입력하여 JWT를 발급받습니다."
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request,
                                   HttpServletResponse response) {

        // 1. 아이디/비번 검증
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUserId(), request.getPassword())
        );

        // 2. 유저 정보 조회
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUserId());

        // 3. JWT 토큰 생성
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // 4. 쿠키 생성
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 15) // 15분
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60 * 24 * 7) // 7일
                .build();

        // 5. 쿠키 응답에 담기
        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        LoginResponseDTO tokenResponse = new LoginResponseDTO("로그인성공" ,accessToken, refreshToken);

        return ResponseEntity.ok(tokenResponse);
    }

    @Operation(
            summary = "회원가입",
            description = "아이디와 비밀번호, 닉네임을 입력하여 회원가입합니다"
    )
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDTO> signUp(@RequestBody SignUpRequestDTO request) {
        SignUpResponseDTO response = userService.signUp(request);
        return ResponseEntity.ok(response);
    }
}
