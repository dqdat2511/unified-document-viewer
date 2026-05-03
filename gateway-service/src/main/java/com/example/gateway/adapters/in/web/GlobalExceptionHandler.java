package com.example.gateway.adapters.in.web;

import com.example.gateway.adapters.in.web.dto.ApiError;
import com.example.gateway.domain.exception.ApplicationErrorCode;
import com.example.gateway.domain.exception.GatewayRuntimeException;
import com.example.gateway.domain.exception.ErrorCodeProvider;
import java.time.Instant;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;
    private final ErrorCodeProvider errorCodeProvider;

    @ExceptionHandler(GatewayRuntimeException.class)
    public ResponseEntity<ApiError> handleGatewayException(GatewayRuntimeException ex, ServerWebExchange exchange) {
        return build(ex.getErrorCode(), ex.getMessage(), exchange);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, ServerWebExchange exchange) {
        ApplicationErrorCode errorCode = errorCodeProvider.getInternalError();
        String message = resolve(errorCode.getMessageKey(), Locale.getDefault());
        return build(errorCode, message, exchange);
    }

    private ResponseEntity<ApiError> build(ApplicationErrorCode errorCode, String message, ServerWebExchange exchange) {
        HttpStatus status = HttpStatus.valueOf(errorCode.getHttpStatusCode());
        String traceId = exchange.getRequest().getHeaders().getFirst("X-Trace-Id");
        ApiError apiError = new ApiError(
            Instant.now(),
            status.value(),
            errorCode.getErrorCode(),
            message,
            exchange.getRequest().getPath().value(),
            traceId
        );
        return ResponseEntity.status(status).body(apiError);
    }

    private String resolve(String code, Locale locale) {
        return messageSource.getMessage(code, null, code, locale);
    }
}