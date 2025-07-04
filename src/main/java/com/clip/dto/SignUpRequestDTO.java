package com.clip.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "회원가입 요청 DTO")
public class SignUpRequestDTO {
    @Schema(description = "아이디", example = "testuser")
    private String userId;
    @Schema(description = "비밀번호", example = "testpass")
    private String password;
    @Schema(description = "닉네임", example = "tester")
    private String nickName;
}
