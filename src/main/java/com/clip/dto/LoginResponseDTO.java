package com.clip.dto;

import lombok.*;
import org.springframework.http.ResponseCookie;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {

    private ResponseCookie accessCookie;
    private ResponseCookie refreshCookie;
    private LoginData loginData;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginData {
        private String message;
        private String nickName;
        private String provider;
    }
}
