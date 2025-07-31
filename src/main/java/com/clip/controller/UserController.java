package com.clip.controller;

import com.clip.config.security.CustomUserDetails;
import com.clip.dto.SocialLoginCheckResponseDTO;
import com.clip.dto.common.Response;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    @Operation(
            summary = "유저 세부 정보",
            description = "유저 세부정보 가져오기"
    )
    @GetMapping("/me")
    public ResponseEntity<Response<SocialLoginCheckResponseDTO>> userInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        SocialLoginCheckResponseDTO result = new SocialLoginCheckResponseDTO(
                userDetails.getUsername(), // userId
                userDetails.getUser().getProvider(),  // "kakao" or "google"
                userDetails.getNickname()
        );

        return ResponseEntity.ok(Response.success(result));
    }
}
