package com.example.gateway.domain.exception;

import lombok.Getter;

@Getter
public class GatewayRuntimeException extends RuntimeException {

    private final ApplicationErrorCode errorCode;

    public GatewayRuntimeException(ApplicationErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public GatewayRuntimeException(ApplicationErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
