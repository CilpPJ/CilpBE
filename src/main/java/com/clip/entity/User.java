package com.clip.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, unique = true)
    private String userId; // 자체 회원가입 아이디

    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String provider; // LOCAL, KAKAO, GOOGLE, NAVER

    @Column(nullable = false, unique = true)
    private String providerId; // ex) Google sub, Kakao id

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Clip> clips = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> Tag = new ArrayList<>();

    // 회원가입/소셜로그인 전용 생성자 빌더
    @Builder
    public User(String userId, String password, String nickname, String provider, String providerId) {
        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
        this.provider = provider;
        this.providerId = providerId;
        this.clips = new ArrayList<>();
        this.Tag = new ArrayList<>();
    }
}
