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
    @Column(length = 50)
    private String userId;  // 유저 ID (로그인용 ID, 이메일 아님)

    @Column(nullable = false, length = 200)
    private String password;

    @Column(nullable = false, length = 50, unique = true)
    private String nickName;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Clip> clips = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> Tag = new ArrayList<>();
}
