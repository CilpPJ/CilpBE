package com.clip.service;

import com.clip.dto.clip.CreateClipRequestDTO;
import com.clip.dto.clip.CreateClipResponseDTO;
import com.clip.entity.Clip;
import com.clip.entity.Tag;
import com.clip.entity.User;
import com.clip.repository.ClipRepository;
import com.clip.repository.TagRepository;
import com.clip.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClipService {
    private final UserRepository userRepository;
    private final ClipRepository clipRepository;
    private final TagRepository tagRepository;

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

        return new CreateClipResponseDTO("클립 생성이 완료되었습니다!");
    }

    private User getUserById(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
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
