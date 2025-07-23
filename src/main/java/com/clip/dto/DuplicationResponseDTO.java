package com.clip.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DuplicationResponseDTO {
    @Schema(name = "isDuplicated", description = "중복 여부")
    private Boolean isDuplicated;
    private String message;
}
