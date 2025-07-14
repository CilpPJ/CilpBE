package com.clip.controller;

import com.clip.dto.clip.*;
import com.clip.dto.common.Response;
import com.clip.service.ClipService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
    public ResponseEntity<Response<CreateClipResponseDTO>> createClip(
            @AuthenticationPrincipal String userId,
            @RequestBody CreateClipRequestDTO request)
    {
        return ResponseEntity.ok(Response.success(clipService.createClip(userId, request)));
    }

    @Operation(
            summary = "클립 전체 조회",
            description = "모든 클립의 제목, 태그이름, 메모, 생성시간과 무한스크롤 관련정보를 가져옵니다,"
    )
    @GetMapping("")
    public ResponseEntity<Response<Slice<GetClipResponseDTO>>> getAllClips(
            @RequestParam(required = false) String lastCreatedAt, // 커서 값
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal String userId)
    {
        return ResponseEntity.ok(Response.success(clipService.getAllClips(userId, lastCreatedAt, size)));
    }

    @Operation(
            summary = "클립 상세 내역 조회",
            description = "특정 클립의 상세 내역을 가져옵니다. "
    )
    @GetMapping("/{clipId}")
    public ResponseEntity<Response<GetDetailClipResponseDTO>> getClip(
            @PathVariable Long clipId,
            @AuthenticationPrincipal String userId
    ){
        return ResponseEntity.ok(Response.success(clipService.getDetailClip(userId, clipId)));
    }

    @Operation(
            summary = "클립 내용 수정",
            description = "특정 클립의 내용을 수정합니다 "
    )
    @PutMapping("/{clipId}")
    public ResponseEntity<Response<UpdateClipResponseDTO>> updateClip(
            @PathVariable Long clipId,
            @RequestBody UpdateClipRequestDTO request,
            @AuthenticationPrincipal String userId
    ){
        return ResponseEntity.ok(Response.success(clipService.updateDetailClip(userId, clipId, request)));
    }

    @Operation(
            summary = "클립 삭제",
            description = "특정 클립을 삭제합니다 "
    )
    @DeleteMapping("/{clipId}")
    public ResponseEntity<Response<DeleteClipResponseDTO>> deleteClip(
            @PathVariable Long clipId,
            @AuthenticationPrincipal String userId
    ){
        return ResponseEntity.ok(Response.success(clipService.deleteClip(userId, clipId)));
    }

}
