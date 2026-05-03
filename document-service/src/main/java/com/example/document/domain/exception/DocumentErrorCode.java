package com.example.document.domain.exception;

import lombok.Getter;

@Getter
public enum DocumentErrorCode implements ApplicationErrorCode {
    VALIDATION("00001", 400, "error.validation"),
    CONSTRAINT("00002", 400, "error.constraint"),
    INTERNAL("00003", 500, "error.internal"),
    DOCUMENT_NOT_FOUND("DOC_00001", 404, "error.document_not_found");


    private final String errorCode;
    private final int httpStatusCode;
    private final String messageKey;

    DocumentErrorCode(String errorCode, int httpStatusCode, String messageKey) {
        this.errorCode = errorCode;
        this.httpStatusCode = httpStatusCode;
        this.messageKey = messageKey;
    }

    @Override
    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    @Override
    public String getMessageKey() {
        return messageKey;
    }
}