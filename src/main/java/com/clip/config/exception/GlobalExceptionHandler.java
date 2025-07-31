package com.clip.config.exception;

import com.clip.dto.common.Response;
import io.swagger.v3.oas.annotations.Hidden;
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

import jakarta.validation.ConstraintViolationException;
import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 🔹 커스텀 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Response<Void>> handleCustomException(CustomException ex) {
        log.warn("[CustomException] {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(Response.fail(ex.getErrorCode(), ex.getMessage()));
    }

    // 🔹 중복 예외 처리 (Custom)
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<Response<Void>> handleDuplicateException(DuplicateException ex) {
        log.warn("[DuplicateException] {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Response.conflict(ex.getErrorCode(), ex.getMessage()));
    }

    // 🔹 데이터 무결성 위반 (JPA/DB)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("[DataIntegrityViolationException]", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Response.fail("DATA_INTEGRITY_ERROR", "DB 저장 중 무결성 제약 위반이 발생했습니다."));
    }

    // 🔹 SQL 제약 위반 (직접 JDBC 예외 처리)
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Response<Void>> handleSQLIntegrity(SQLIntegrityConstraintViolationException ex) {
        log.error("[SQLIntegrityConstraintViolationException]", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Response.fail("SQL_CONSTRAINT_ERROR", "DB 제약 조건 위반: " + ex.getMessage()));
    }

    // 🔹 중복 키 예외 (PK/Unique Index 중복)
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Response<Void>> handleDuplicateKey(DuplicateKeyException ex) {
        log.warn("[DuplicateKeyException] {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Response.fail("DUPLICATE_KEY", "중복된 데이터가 존재합니다."));
    }

    // 🔹 잘못된 요청 파라미터 (필수값 누락)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Response<Void>> handleMissingParam(MissingServletRequestParameterException ex) {
        log.warn("[MissingServletRequestParameter] {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(Response.fail("MISSING_PARAMETER", "필수 요청 파라미터가 누락되었습니다: " + ex.getParameterName()));
    }

    // 🔹 메서드 유효성 검증 실패 (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("[ValidationError] {}", ex.getMessage());
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("유효성 검사 실패");
        return ResponseEntity
                .badRequest()
                .body(Response.fail("VALIDATION_ERROR", errorMessage));
    }

    // 🔹 ConstraintViolationException (GET/RequestParam 유효성 실패)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("[ConstraintViolation] {}", ex.getMessage());
        String errorMessage = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .findFirst()
                .orElse("요청 파라미터 유효성 검사 실패");
        return ResponseEntity
                .badRequest()
                .body(Response.fail("VALIDATION_ERROR", errorMessage));
    }

    // 🔹 BindException (폼 데이터/쿼리 파라미터 바인딩 실패)
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Response<Void>> handleBindException(BindException ex) {
        log.warn("[BindException] {}", ex.getMessage());
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("요청 값 바인딩 실패");
        return ResponseEntity
                .badRequest()
                .body(Response.fail("BIND_ERROR", errorMessage));
    }

    // 🔹 잘못된 JSON 포맷 (RequestBody 파싱 실패)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response<Void>> handleJsonParseError(HttpMessageNotReadableException ex) {
        log.warn("[JsonParseError] {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(Response.fail("INVALID_JSON", "요청 본문이 올바른 JSON 형식이 아닙니다."));
    }

    // 🔹 지원되지 않는 HTTP 메서드
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Response<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.warn("[MethodNotSupported] {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Response.fail("METHOD_NOT_ALLOWED", "지원하지 않는 HTTP 메서드입니다."));
    }

    // 🔹 인증/인가 실패 (Spring Security)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response<Void>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("[AccessDenied] {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Response.fail("ACCESS_DENIED", "접근 권한이 없습니다."));
    }

    // 🔹 파일 업로드 크기 초과
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Response<Void>> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        log.warn("[MaxUploadSizeExceeded] {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(Response.fail("UPLOAD_SIZE_EXCEEDED", "업로드 가능한 파일 크기를 초과했습니다."));
    }

    // 🔹 NullPointerException (개발 실수)
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Response<Void>> handleNPE(NullPointerException ex) {
        log.error("[NullPointerException]", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("NullPointerException: " + ex.getMessage()));
    }

    // 🔹 예상치 못한 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleAllExceptions(Exception ex) {
        log.error("[Unhandled Exception]", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("예상치 못한 오류가 발생했습니다."));
    }
}
