package com.clip.dto.clip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "클립 내용 업데이트 요청 DTO")
public class UpdateClipRequestDTO {
    @Schema(description = "수정된 제목", example = "수정된 제목")
    private String title;
    @Schema(description = "수정된 url", example = "수정된 url")
    private String url;
    @Schema(description = "수정된 태그", example = "수정된 태그이름")
    private String tagName;
    @Schema(description = "수정된 메모", example = "수정된 메모")
    private String memo;
}
