package com.clip.dto.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "친구 요청 DTO")
public class SendFriendRequestDTO {
    @Schema(description = "요청 받는 사용자 닉네임", example = "강민짱")
    private String nickName;
}
