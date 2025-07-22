package com.clip.controller;

import com.clip.config.security.CustomUserDetails;
import com.clip.dto.*;
import com.clip.dto.common.Response;
import com.clip.service.AuthService;
import com.clip.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @Operation(
            summary = "로그인",
            description = "아이디와 비밀번호를 입력하여 JWT를 발급받습니다."
    )
    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponseDTO.LoginData>> login(
            @RequestBody LoginRequestDTO request
    ) {
        LoginResponseDTO response = authService.login(request);

        return ResponseEntity.ok()
                .header("Set-Cookie", response.getAccessCookie().toString())
                .header("Set-Cookie", response.getRefreshCookie().toString())
                .body(Response.success(response.getLoginData()));
    }

    @Operation(
            summary = "회원가입",
            description = "아이디와 비밀번호, 닉네임을 입력하여 회원가입합니다"
    )
    @PostMapping("/signup")
    public ResponseEntity<Response<SignUpResponseDTO>> signUp(@RequestBody SignUpRequestDTO request) {
        return ResponseEntity.ok(Response.success(userService.signUp(request)));
    }

    @Operation(
            summary = "아이디 중복확인",
            description = "아이디 중복확인을 합니다"
    )
    @PostMapping("/check/duplicateId/{userId}")
    public ResponseEntity<Response<DuplicationResponseDTO>> userIdDuplicationCheck(@PathVariable String userId){
        return ResponseEntity.ok(Response.success(userService.checkUserId(userId)));
    }

    @Operation(
            summary = "닉네임 중복확인",
            description = "닉네임 중복확인을 합니다"
    )
    @PostMapping("/check/duplicateNickName/{nickName}")
    public ResponseEntity<Response<DuplicationResponseDTO>> nickNameDuplicationCheck(@PathVariable String nickName){
        return ResponseEntity.ok(Response.success(userService.checkNickName(nickName)));
    }

    @Operation(
            summary = "유저 정보 가져오기",
            description = "유저 정보를 가져옵니다"
    )
    @GetMapping("/me")
    public ResponseEntity<Response<CustomUserDetails>> checkLogin(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        return ResponseEntity.ok(Response.success(userDetails));
    }
}
