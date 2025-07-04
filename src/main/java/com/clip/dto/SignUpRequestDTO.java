package com.clip.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "회원가입 요청 DTO")
public class SignUpRequestDTO {
    private String userId;
    private String password;
    private String nickName;
}
