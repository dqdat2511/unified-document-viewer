package com.example.document.domain.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class DocumentRuntimeException extends RuntimeException {

    public static final String EXTRA_DATA_VIN = "vin";
    public static final String EXTRA_DATA_HTTP_STATUS_CODE = "httpStatusCode";
    public static final String EXTRA_DATA_HTTP_ERROR_RESPONSE = "httpErrorResponse";

    private final ApplicationErrorCode errorCode;
    private final Object[] messageArgs;
    private final Map<String, Object> extraData = new HashMap<>();

    public void addExtraData(String key, Object value) {
        this.extraData.put(key, value);
    }

    public DocumentRuntimeException(ApplicationErrorCode errorCode, String message, Object... messageArgs) {
        super(message);
        this.errorCode = errorCode;
        this.messageArgs = messageArgs;
    }

    public DocumentRuntimeException(ApplicationErrorCode errorCode, String message, Throwable cause, Object... messageArgs) {
        super(message, cause);
        this.errorCode = errorCode;
        this.messageArgs = messageArgs;
    }

    public DocumentRuntimeException(ApplicationErrorCode errorCode, Throwable cause, Object... messageArgs) {
        super(cause);
        this.errorCode = errorCode;
        this.messageArgs = messageArgs;
    }

    public DocumentRuntimeException(
            ApplicationErrorCode errorCode,
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace,
            Object... messageArgs) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
        this.messageArgs = messageArgs;
    }
}