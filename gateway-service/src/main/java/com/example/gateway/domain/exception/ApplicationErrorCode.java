package com.example.gateway.domain.exception;

public interface ApplicationErrorCode {
    String getErrorCode();
    int getHttpStatusCode();
    String getMessageKey();
}
