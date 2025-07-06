package com.clip.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DuplicationResponseDTO {
    private boolean isDuplicated;
    private String message;

    public DuplicationResponseDTO(boolean isDuplicated, String message) {
        this.isDuplicated = isDuplicated;
        this.message = message;
    }
}
