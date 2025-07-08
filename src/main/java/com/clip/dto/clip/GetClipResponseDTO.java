package com.clip.dto.clip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetClipResponseDTO {
    private String title;
    private Long tagId;
    private String tagName;
    private String memo;
    private LocalDateTime createdAt;
}
