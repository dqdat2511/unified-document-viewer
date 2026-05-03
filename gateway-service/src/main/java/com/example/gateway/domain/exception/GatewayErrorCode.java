package com.example.gateway.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GatewayErrorCode implements ApplicationErrorCode {
    BAD_GATEWAY("00001", 502, "error.bad_gateway"),
    TIMEOUT("00002", 504, "error.timeout"),
    INTERNAL("00003", 500, "error.internal");

    private final String errorCode;
    private final int httpStatusCode;
    private final String messageKey;
}
