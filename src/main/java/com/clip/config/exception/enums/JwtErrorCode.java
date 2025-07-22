package com.clip.config.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtErrorCode {
    MALFORMED_TOKEN("JWT_001", "잘못된 형식의 JWT입니다."),
    EXPIRED_TOKEN("JWT_002", "JWT가 만료되었습니다."),
    INVALID_SIGNATURE("JWT_003", "JWT 서명이 유효하지 않습니다."),
    EMPTY_TOKEN("JWT_004", "JWT가 존재하지 않거나 빈 값입니다."),
    INVALID_USER("JWT_005", "JWT에서 추출한 유저 정보가 잘못되었습니다.");

    private final String code;
    private final String message;
}
