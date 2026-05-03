package com.example.auth.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ApplicationErrorCode {
    VALIDATION("00001", 400, "error.validation"),
    CONSTRAINT("00002", 400, "error.constraint"),
    UNAUTHORIZED("00003", 401, "error.unauthorized"),
    INTERNAL("00004", 500, "error.internal"),
    USERNAME_TAKEN("00005", 409, "error.username.taken");

    private final String errorCode;
    private final int httpStatusCode;
    private final String messageKey;
}
