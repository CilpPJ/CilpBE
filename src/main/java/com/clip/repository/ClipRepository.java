package com.clip.repository;

import com.clip.dto.clip.GetClipResponseDTO;
import com.clip.entity.Clip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClipRepository extends JpaRepository<Clip, Long> {
    @Query("SELECT new com.clip.dto.clip.GetClipResponseDTO(c.title, c.tag.tagId, c.tag.tagName, c.memo, c.createdAt) " +
            "FROM Clip c " +
            "WHERE c.user.userId = :userId ")
    Page<GetClipResponseDTO> findAllClipByUserId(@Param("userId") String userId, Pageable pageable);
}
