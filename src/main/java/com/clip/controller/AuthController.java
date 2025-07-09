package com.clip.controller;

import com.clip.config.security.CustomUserDetails;
import com.clip.config.security.CustomUserDetailsService;
import com.clip.config.security.JwtService;
import com.clip.dto.*;
import com.clip.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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

        // 4. 쿠키 생성 (운영할때 반드시 secure설정을 true로해서 https만 가능하게 해야한다)
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60 * 15) // 15분
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60 * 60 * 24 * 7) // 7일
                .build();

        // 5. 응답 구성
        return ResponseEntity.ok()
                .header("Set-Cookie", accessCookie.toString())
                .header("Set-Cookie", refreshCookie.toString())
                .body(new LoginResponseDTO("로그인 성공"));
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

    @Operation(
            summary = "아이디 중복확인",
            description = "아이디 중복확인을 합니다"
    )
    @PostMapping("/check/duplicateId/{userId}")
    public ResponseEntity<DuplicationResponseDTO> userIdDuplicationCheck(@PathVariable String userId){
        DuplicationResponseDTO response = userService.checkUserId(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "닉네임 중복확인",
            description = "닉네임 중복확인을 합니다"
    )
    @PostMapping("/check/duplicateNickName/{nickName}")
    public ResponseEntity<DuplicationResponseDTO> nickNameDuplicationCheck(@PathVariable String nickName){
        DuplicationResponseDTO response = userService.checkNickName(nickName);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "유저 정보 가져오기",
            description = "유저 정보를 가져옵니다"
    )
    @GetMapping("/me")
    public ResponseEntity<UserDTO> checkLogin(
            @AuthenticationPrincipal String userId
    ){
        UserDTO response = userService.getUser(userId);
        return ResponseEntity.ok(response);
    }
}
