package com.clip.repository;

import com.clip.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserId(String userId);
    Optional<User> findByNickName(String nickName);
    boolean existsByUserId(String userId);
    boolean existsByNickName(String nickName);
}
