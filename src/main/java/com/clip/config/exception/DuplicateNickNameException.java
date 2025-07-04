package com.clip.config.exception;

public class DuplicateNickNameException extends RuntimeException {
    public DuplicateNickNameException(String nickName) {
        super("이미 사용 중인 닉네임입니다: " + nickName);
    }
}