package com.clip.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @Column(length = 50)
    private String userId;  // 유저 ID (로그인용 ID, 이메일 아님)

    @Column(nullable = false, length = 200)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickName;
}
