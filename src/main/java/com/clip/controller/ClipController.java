package com.clip.controller;

import com.clip.dto.clip.*;
import com.clip.service.ClipService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clips")
@RequiredArgsConstructor
public class ClipController {

    private final ClipService clipService;

    @Operation(
            summary = "클립 생성",
            description = "클립을 생성하는 api입니다"
    )
    @PostMapping("")
    public ResponseEntity<CreateClipResponseDTO> createClip(
            @AuthenticationPrincipal String userId,
            @RequestBody CreateClipRequestDTO request)
    {
        CreateClipResponseDTO response = clipService.createClip(userId, request);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "클립 전체 조회",
            description = "모든 클립의 제목, 태그이름, 메모, 생성시간과 무한스크롤 관련정보를 가져옵니다,"
    )
    @GetMapping("")
    public Page<GetClipResponseDTO> getAllClips(
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal String userId)
    {
        Page<GetClipResponseDTO> responsePage = clipService.getAllClips(userId, pageable);
        return responsePage;
    }

    @Operation(
            summary = "클립 상세 내역 조회",
            description = "특정 클립의 상세 내역을 가져옵니다. "
    )
    @GetMapping("/{clipId}")
    public ResponseEntity<GetDetailClipResponseDTO> getClip(
            @PathVariable Long clipId,
            @AuthenticationPrincipal String userId
    ){
        GetDetailClipResponseDTO response = clipService.getDetailClip(userId, clipId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "클립 내용 수정",
            description = "특정 클립의 내용을 수정합니다 "
    )
    @PutMapping("/{clipId}")
    public ResponseEntity<UpdateClipResponseDTO> updateClip(
            @PathVariable Long clipId,
            @RequestBody UpdateClipRequestDTO request,
            @AuthenticationPrincipal String userId
    ){
        UpdateClipResponseDTO response = clipService.updateDetailClip(userId, clipId, request);
        return ResponseEntity.ok(response);
    }

}
