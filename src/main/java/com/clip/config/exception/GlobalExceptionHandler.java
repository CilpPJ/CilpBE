package com.clip.config.exception;

import com.clip.dto.common.Response;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    // --- Custom domain exceptions ---
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Response<Void>> handleCustomException(CustomException ex) {
        log.warn("[CustomException] {}", ex.getMessage());
        return ResponseEntity.badRequest().body(Response.fail(ex.getErrorCode(), ex.getMessage()));
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<Response<Void>> handleDuplicateException(DuplicateException ex) {
        log.warn("[DuplicateException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Response.conflict(ex.getErrorCode(), ex.getMessage()));
    }

    // --- 400 Bad Request 계열 ---
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst().orElse("입력값을 확인해주세요.");
        return ResponseEntity.badRequest().body(Response.fail("REQ.VALIDATION", msg));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .findFirst().orElse("요청 파라미터 유효성 검사 실패");
        return ResponseEntity.badRequest().body(Response.fail("REQ.VALIDATION", msg));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Response<Void>> handleBind(BindException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst().orElse("요청 값 바인딩 실패");
        return ResponseEntity.badRequest().body(Response.fail("REQ.VALIDATION", msg));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Response<Void>> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest()
                .body(Response.fail("REQ.MISSING_PARAM", "필수 파라미터 누락: " + ex.getParameterName()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response<Void>> handleInvalidJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(Response.fail("REQ.INVALID_JSON", "요청 본문이 올바른 JSON 형식이 아닙니다."));
    }

    // --- 403 Forbidden ---
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response<Void>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Response.fail("AUTH.FORBIDDEN", "접근 권한이 없습니다."));
    }

    // --- 405 Method Not Allowed ---
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Response<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Response.fail("REQ.METHOD_NOT_ALLOWED", "지원하지 않는 HTTP 메서드입니다."));
    }

    // --- 409 Conflict / 데이터 제약 ---
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Response<Void>> handleDuplicateKey(DuplicateKeyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Response.fail("DATA.DUPLICATE", "이미 존재하는 데이터입니다."));
    }

    @ExceptionHandler({DataIntegrityViolationException.class, SQLIntegrityConstraintViolationException.class})
    public ResponseEntity<Response<Void>> handleDataConstraint(Exception ex) {
        // 중복/제약 위반을 하나로 취급 (필요하면 메시지에서 분기)
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Response.fail("DATA.CONSTRAINT", "데이터 제약 조건을 위반했습니다."));
    }

    // --- 413 Payload Too Large ---
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Response<Void>> handleMaxUpload(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(Response.fail("UPLOAD.TOO_LARGE", "업로드 가능한 파일 크기를 초과했습니다."));
    }

    // --- 500 Internal Server Error ---
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Response<Void>> handleNpe(NullPointerException ex) {
        log.error("[NullPointerException]", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("NullPointerException: " + ex.getMessage()));
        // Response.error => status="ERROR", errorCode="INTERNAL_ERROR"
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleAll(Exception ex) {
        log.error("[Unhandled Exception]", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(ex.getMessage()));
    }
}
