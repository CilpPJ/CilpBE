package com.clip.repository;

import com.clip.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    boolean existsByFromIdAndToId(String fromId, String toId); // 내가 보낸 요청
    @Query("SELECT f " +
            "FROM Friend f " +
            "WHERE (f.fromId = :fromId AND f.toId = :toId) OR (f.fromId = :toId AND f.toId = :fromId)")
    Optional<Friend> findFriendRelation(String fromId, String toId);
}
