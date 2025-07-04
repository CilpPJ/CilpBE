package com.clip.service;

import com.clip.config.exception.DuplicateUserIdException;
import com.clip.dto.SignUpRequestDTO;
import com.clip.dto.SignUpResponseDTO;
import com.clip.entity.User;
import com.clip.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignUpResponseDTO signUp(SignUpRequestDTO request) {
        if (userRepository.existsById(request.getUserId())){
            throw new DuplicateUserIdException(request.getUserId());
        }

        User user = User.builder()
                .userId(request.getUserId())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickName(request.getNickName())
                .build();

        userRepository.save(user);

        return new SignUpResponseDTO("회원가입이 완료되었습니다.");
    }
}
