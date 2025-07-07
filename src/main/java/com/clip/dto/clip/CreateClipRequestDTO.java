package com.clip.dto.clip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "클립 생성 DTO")
public class CreateClipRequestDTO {

    @Schema(description = "클립 제목", example = "나중에 꼭 다시 보고 싶은 영상")
    private String title;

    @Schema(description = "클립 URL", example = "https://www.youtube.com/watch?v=...")
    private String url;

    @Schema(description = "클립 메모", example = "핵심 내용은 3분 20초부터!")
    private String memo;

    @Schema(description = "태그 이름", example = "생성형AI", nullable = true)
    private String tagName;
}
