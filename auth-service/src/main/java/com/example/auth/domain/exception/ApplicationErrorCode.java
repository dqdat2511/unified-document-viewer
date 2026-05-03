package com.example.auth.domain.exception;

public interface ApplicationErrorCode {
    String getErrorCode();
    int getHttpStatusCode();
    String getMessageKey();
}
