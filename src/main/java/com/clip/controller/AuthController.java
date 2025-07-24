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
            summary = "카카오 로그인 URL 반환",
            description = """
            프론트엔드에서 로그인 버튼 클릭 시, 이 API를 호출해 로그인 시작 URL을 받아갑니다.
            응답으로 받은 URL로 브라우저 이동하면 소셜 로그인이 시작됩니다.
            리다이렉트 URI는 백엔드에서 관리하므로 프론트는 신경 쓸 필요 없습니다.
        """
    )
    @GetMapping("/kakao-url")
    public ResponseEntity<String> getKakaoLoginUrl() {
        String redirectUrl = "http://www.clipin.store/oauth2/authorization/kakao";
        return ResponseEntity.ok(redirectUrl);
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
    @PostMapping("/check/duplicateNickname/{nickname}")
    public ResponseEntity<Response<DuplicationResponseDTO>> nicknameDuplicationCheck(@PathVariable String nickname){
        return ResponseEntity.ok(Response.success(userService.checkNickName(nickname)));
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
