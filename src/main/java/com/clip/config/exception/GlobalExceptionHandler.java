package com.clip.config.exception;

import com.clip.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 아이디 중복
    @ExceptionHandler(DuplicateUserIdException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponseDTO> handleDuplicateUserId(DuplicateUserIdException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO("USER_ID_DUPLICATE", ex.getMessage()));
    }

    // 닉네임 중복
    @ExceptionHandler(DuplicateNickNameException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponseDTO> handleDuplicateNickName(DuplicateNickNameException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO("NICKNAME_DUPLICATE", ex.getMessage()));
    }

    // 유저를 찾을수 없음
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDTO("USER_NOT_FOUND", ex.getMessage()));
    }

    // 자기 자신한테 친구 요청
    @ExceptionHandler(AskMeFriendRequestException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponseDTO> handleAskMeFriendRequest(AskMeFriendRequestException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO("ASK_ME_FRIEND_REQUEST", ex.getMessage()));
    }

    // 친구 요청을 이미 보냄
    @ExceptionHandler(AlreadyRequestedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAlreadyRequested(AlreadyRequestedException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO("ALREADY_REQUESTED", ex.getMessage()));
    }

    // 이미 친구상태
    @ExceptionHandler(AlreadyFriendException.class)
    public ResponseEntity<ErrorResponseDTO> handleAlreadyFriend(AlreadyFriendException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO("ALREADY_FRIEND", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(Exception ex) {
        ex.printStackTrace(); // 콘솔 로그에 예외 전체 출력

        // logger로 남기고 사용자에겐 내부 메시지 숨김 - 운영용 코드
        //log.error("Internal server error occurred", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO(
                        "INTERNAL_ERROR",
                        ex.getClass().getSimpleName() + " : " + ex.getMessage()
                ));
    }
}