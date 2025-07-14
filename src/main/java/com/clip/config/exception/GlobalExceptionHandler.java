package com.clip.config.exception;

import com.clip.dto.common.Response;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 커스텀 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Response<Void>> handleCustomException(CustomException ex) {
        return ResponseEntity
                .badRequest()
                .body(Response.fail(ex.getErrorCode(), ex.getMessage()));
    }

    // 널포인터 등 서버 내부 에러 같은 코드상 실수
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Response<Void>> handleNPE(NullPointerException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("NullPointerException: " + ex.getMessage()));
    }

    // 기타 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleAllExceptions(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("예상치 못한 오류가 발생했습니다."));
    }
}