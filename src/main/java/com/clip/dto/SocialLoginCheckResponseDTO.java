package com.clip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SocialLoginCheckResponseDTO {
    private String userId;
    private String provider;
    private String nickname;
}
