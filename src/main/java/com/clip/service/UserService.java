package com.clip.service;

import com.clip.config.exception.DuplicateNickNameException;
import com.clip.config.exception.DuplicateUserIdException;
import com.clip.dto.DuplicationResponseDTO;
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

    public DuplicationResponseDTO checkUserId(String userId) {

       boolean isDuplication = userRepository.existsByUserId(userId);

       if (isDuplication){
           throw new DuplicateUserIdException(userId);
       }

       return new DuplicationResponseDTO(isDuplication, "사용 가능한 값입니다.");
    }

    public DuplicationResponseDTO checkNickName(String nickName) {
        boolean isDuplication = userRepository.existsByNickName(nickName);

        if (isDuplication){
            throw new DuplicateNickNameException(nickName);
        }

        return new DuplicationResponseDTO(isDuplication, "사용 가능한 닉네임입니다.");
    }

}
