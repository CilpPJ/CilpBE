package com.clip.service;

import com.clip.config.exception.CustomException;
import com.clip.dto.clip.*;
import com.clip.entity.Clip;
import com.clip.entity.Tag;
import com.clip.entity.User;
import com.clip.repository.ClipRepository;
import com.clip.repository.TagRepository;
import com.clip.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClipService {
    private final UserRepository userRepository;
    private final ClipRepository clipRepository;
    private final TagRepository tagRepository;

    // 클립 생성하기
    @Transactional
    public CreateClipResponseDTO createClip(String userId, CreateClipRequestDTO request) {
        User user = getUserById(userId);
        Tag tag = getOrCreateTag(request.getTagName(), user);

        Clip clip = Clip.builder()
                .title(request.getTitle())
                .url(request.getUrl())
                .memo(request.getMemo())
                .user(user)
                .tag(tag)
                .build();

        clipRepository.save(clip);

        return new CreateClipResponseDTO("클립 생성이 완료되었습니다");
    }

    // 모든 클립 무한스크롤 기반으로 가져오기
    public Slice<GetClipResponseDTO> getAllClips(String userId, String lastCreatedAt, int size){
        LocalDateTime cursor = lastCreatedAt != null
                ? LocalDateTime.parse(lastCreatedAt)
                : LocalDateTime.now(); // 첫 페이지면 지금 기준

        return clipRepository.findByUserIdAndCreatedAtBefore(userId, cursor,
                PageRequest.of(0, size, Sort.by("createdAt").descending()));
    }

    // 클립 상세 내역 조회
    public GetDetailClipResponseDTO getDetailClip(String userId, Long clipId){

        return clipRepository.findClipByUserIdAndClipId(userId, clipId)
                .orElseThrow(() -> new CustomException("CLIP_NOT_FOUND", "해당 클립이 존재하지 않습니다"));
    }

    // 클립 내용 수정
    @Transactional
    public UpdateClipResponseDTO updateDetailClip(String userId, Long clipId, UpdateClipRequestDTO request){
        // 클립 내용 가져오기(그 클립이 있는지부터 확인)
        Clip clip = clipRepository.findByUser_UserIdAndClipId(userId, clipId)
                .orElseThrow(() -> new CustomException("CLIP_NOT_FOUND", "해당 클립이 존재하지 않습니다"));

        User user = getUserById(userId);
        Tag tag = getOrCreateTag(request.getTagName(), user);

        // 도메인 메서드로 필드 변경
        clip.updateClipInfo(request.getTitle(), request.getUrl(), request.getMemo(), tag);

        // @Transactional + dirty checking으로 자동 저장
        return new UpdateClipResponseDTO("클립이 성공적으로 수정되었습니다.", clip.getClipId());
    }

    //클립 삭제
    @Transactional
    public DeleteClipResponseDTO deleteClip(String userId, Long clipId){
        // 해당 클립이 존재하고, 내가 소유자인지 확인
        Clip clip = clipRepository.findByUser_UserIdAndClipId(userId, clipId)
                .orElseThrow(() -> new CustomException("CLIP_NOT_OWN", "해당 클립을 소유하고 있지 않습니다"));

        clipRepository.delete(clip);

        return new DeleteClipResponseDTO("클립 삭제 완료");
    }

    private User getUserById(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "해당 유저가 존재하지 않습니다"));
    }

    private Tag getOrCreateTag(String tagName, User user){

        Optional<Tag> existingTag = tagRepository.findByTagNameAndUser(tagName, user);

        if (existingTag.isPresent()) {
            return existingTag.get();
        } else {
            Tag newTag = Tag.builder()
                    .tagName(tagName)
                    .user(user)
                    .build();

            return tagRepository.save(newTag);
        }
    }

}
