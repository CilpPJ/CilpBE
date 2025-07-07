package com.clip.repository;

import com.clip.entity.Clip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClipRepository extends JpaRepository<Clip, Long> {
}
