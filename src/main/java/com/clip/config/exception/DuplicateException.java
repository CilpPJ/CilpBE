package com.clip.config.exception;

import lombok.Getter;

@Getter
public class DuplicateException extends RuntimeException {
    private final String errorCode;

    public DuplicateException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
