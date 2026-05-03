package com.example.document.domain.exception;

public interface ApplicationErrorCode {
    String getErrorCode();
    int getHttpStatusCode();
    String getMessageKey();
}
