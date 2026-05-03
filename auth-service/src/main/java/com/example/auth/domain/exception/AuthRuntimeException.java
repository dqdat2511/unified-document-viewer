package com.example.auth.domain.exception;

import lombok.Getter;

@Getter
public class AuthRuntimeException extends RuntimeException {

    private final ApplicationErrorCode errorCode;

    public AuthRuntimeException(ApplicationErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AuthRuntimeException(ApplicationErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
