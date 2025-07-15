package com.clip.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, unique = true)
    private String userId; // 이메일 또는 자체 아이디

    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    private String nickName;

    @Column(nullable = false)
    private String provider; // LOCAL, KAKAO, GOOGLE, NAVER

    @Column(nullable = false, unique = true)
    private String providerId; // ex) Google sub, Kakao id

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Clip> clips = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> Tag = new ArrayList<>();
}
