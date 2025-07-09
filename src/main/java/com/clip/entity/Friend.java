package com.clip.entity;

import com.clip.entity.enums.FriendStatusType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "friend")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Friend extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long friendId;

    @Column(nullable = false, length = 100)
    private String fromId;

    @Column(nullable = false, length = 100)
    private String toId;

    @Column
    @Enumerated(EnumType.STRING)
    private FriendStatusType status;
}
