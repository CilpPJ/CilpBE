package com.clip.service;

import com.clip.config.exception.CustomException;
import com.clip.config.security.CustomUserDetails;
import com.clip.config.security.CustomUserDetailsService;
import com.clip.dto.DuplicationResponseDTO;
import com.clip.dto.SignUpRequestDTO;
import com.clip.dto.SignUpResponseDTO;
import com.clip.dto.UserDTO;
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
    private final CustomUserDetailsService customUserDetailsService;

    //회원가입
    public SignUpResponseDTO signUp(SignUpRequestDTO request) {
        if (userRepository.existsById(request.getUserId())){
            throw new CustomException("DUPLICATION_ID", "아이디가 중복됩니다");
        }

        User user = User.builder()
                .userId(request.getUserId())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickName(request.getNickName())
                .build();

        userRepository.save(user);

        return new SignUpResponseDTO("회원가입이 완료되었습니다.");
    }

    // 중복체크
    public DuplicationResponseDTO checkUserId(String userId) {

       boolean isDuplication = userRepository.existsByUserId(userId);

       if (isDuplication){
           throw new CustomException("DUPLICATION_ID", "아이디가 중복됩니다");
       }

       return new DuplicationResponseDTO(isDuplication, "사용 가능한 값입니다.");
    }

    public DuplicationResponseDTO checkNickName(String nickName) {
        boolean isDuplication = userRepository.existsByNickName(nickName);

        if (isDuplication){
            throw new CustomException("DUPLICATION_NICKNAME", "닉네임이 중복됩니다");
        }

        return new DuplicationResponseDTO(isDuplication, "사용 가능한 닉네임입니다.");
    }

    // 유저정보 가져오기
    public UserDTO getUser(String userId) {
        if (userId == null) {
            return new UserDTO(false, null, null);
        }

        CustomUserDetails userDetails =
                (CustomUserDetails) customUserDetailsService.loadUserByUsername(userId);

        return new UserDTO(true, userDetails.getUsername(), userDetails.getNickName());
    }

}
