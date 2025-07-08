package com.clip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clip")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Clip extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clipId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 200)
    private String url;

    @Column(length = 200)
    private String memo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tagId")
    private Tag tag;

    public void updateClipInfo(String title, String url, String memo, Tag tag) {
        this.title = title;
        this.url = url;
        this.memo = memo;
        this.tag = tag;
    }

}
