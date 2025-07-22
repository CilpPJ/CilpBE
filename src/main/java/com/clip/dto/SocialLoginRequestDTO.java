package com.clip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SocialLoginRequestDTO {
    private String provider;    // "KAKAO", "GOOGLE", ...
    private String providerId;  // 고유 식별자 (ex. sub, id)
    private String email;       // 선택 (우리가 userId로 쓸 수 있음)
}

