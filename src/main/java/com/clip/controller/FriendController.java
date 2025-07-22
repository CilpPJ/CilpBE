package com.clip.controller;

import com.clip.config.security.CustomUserDetails;
import com.clip.dto.common.Response;
import com.clip.dto.friend.SendFriendRequestDTO;
import com.clip.dto.friend.SendFriendResponseDTO;
import com.clip.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;

    @Operation(
            summary = "친구 요청",
            description = "친구 요청을 보내는 api입니다"
    )
    @PostMapping("")
    public ResponseEntity<Response<SendFriendResponseDTO>> sendFriendRequest(
            @RequestBody SendFriendRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails)
    {
        return ResponseEntity.ok(Response.success(friendService.sendFriendRequest(userDetails.getUsername(), request)));
    }
}
