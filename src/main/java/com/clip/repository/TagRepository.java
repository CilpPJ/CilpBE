package com.clip.repository;

import com.clip.entity.Tag;
import com.clip.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByTagNameAndUser(String tagName, User user);
}
