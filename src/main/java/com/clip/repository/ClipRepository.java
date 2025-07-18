package com.clip.repository;

import com.clip.dto.clip.GetClipResponseDTO;
import com.clip.dto.clip.GetDetailClipResponseDTO;
import com.clip.entity.Clip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ClipRepository extends JpaRepository<Clip, Long> {
    @Query("SELECT new com.clip.dto.clip.GetClipResponseDTO(" +
            "c.title, c.tag.tagId, c.tag.tagName, c.url, c.memo, c.createdAt) " +
            "FROM Clip c " +
            "WHERE c.user.userId = :userId AND c.createdAt < :cursor " +
            "ORDER BY c.createdAt DESC")
    Slice<GetClipResponseDTO> findByUserIdAndCreatedAtBefore(
            @Param("userId") String userId,
            @Param("cursor") LocalDateTime cursor,
            Pageable pageable);

    @Query("SELECT new com.clip.dto.clip.GetDetailClipResponseDTO(" +
            "c.clipId, c.title, c.url, c.memo, c.tag.tagId, c.tag.tagName, c.createdAt) " +
            "FROM Clip c " +
            "WHERE c.user.userId = :userId AND c.clipId = :clipId")
    Optional<GetDetailClipResponseDTO> findClipByUserIdAndClipId(
            @Param("userId") String userId,
            @Param("clipId") Long clipId);

    Optional<Clip> findByUser_UserIdAndClipId(String userId, Long clipId);
}
