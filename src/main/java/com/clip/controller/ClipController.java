package com.clip.controller;

import com.clip.dto.clip.CreateClipRequestDTO;
import com.clip.dto.clip.CreateClipResponseDTO;
import com.clip.service.ClipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clips")
@RequiredArgsConstructor
public class ClipController {

    private final ClipService clipService;

    @Operation(
            summary = "클립 생성",
            description = "클립을 생성하는 api입니다"
    )
    @PostMapping("/")
    public ResponseEntity<CreateClipResponseDTO> createClip(
            @AuthenticationPrincipal String userId,
            @RequestBody CreateClipRequestDTO request)
    {
        CreateClipResponseDTO response = clipService.createClip(userId, request);

        return ResponseEntity.ok(response);
    }

}
