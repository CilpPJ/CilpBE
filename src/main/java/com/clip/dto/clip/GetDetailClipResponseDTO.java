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
public class GetDetailClipResponseDTO {
    private Long clipId;
    private String title;
    private String url;
    private String memo;
    private Long tagId;
    private String tagName;
    private LocalDateTime createdAt;
}
