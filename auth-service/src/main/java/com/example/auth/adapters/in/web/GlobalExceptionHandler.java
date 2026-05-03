package com.example.auth.adapters.in.web;

import com.example.auth.adapters.in.web.dto.ApiError;
import com.example.auth.adapters.in.web.filter.TraceIdFilter;
import com.example.auth.domain.exception.ApplicationErrorCode;
import com.example.auth.domain.exception.AuthRuntimeException;
import com.example.auth.domain.exception.ErrorCodeProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;
    private final ErrorCodeProvider errorCodeProvider;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ApplicationErrorCode errorCode = errorCodeProvider.getValidationError();
        String detail = ex.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(error -> error.getField() + " " + error.getDefaultMessage())
            .orElse(resolve(errorCode.getMessageKey(), request.getLocale()));
        return build(errorCode, detail, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        ApplicationErrorCode errorCode = errorCodeProvider.getConstraintError();
        String detail = resolve(errorCode.getMessageKey(), request.getLocale()) + ": " + ex.getMessage();
        return build(errorCode, detail, request);
    }

    @ExceptionHandler(AuthRuntimeException.class)
    public ResponseEntity<ApiError> handleAuthRuntimeException(AuthRuntimeException ex, HttpServletRequest request) {
        log.warn("[{}] {} {}", ex.getErrorCode().getErrorCode(), request.getMethod(), request.getRequestURI(), ex);
        return build(ex.getErrorCode(), ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception {} {}", request.getMethod(), request.getRequestURI(), ex);
        ApplicationErrorCode errorCode = errorCodeProvider.getInternalError();
        return build(errorCode, resolve(errorCode.getMessageKey(), request.getLocale()), request);
    }

    private ResponseEntity<ApiError> build(ApplicationErrorCode errorCode, String message, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(errorCode.getHttpStatusCode());
        String traceId = (String) request.getAttribute(TraceIdFilter.TRACE_ID);
        ApiError apiError = new ApiError(
            Instant.now(),
            status.value(),
            errorCode.getErrorCode(),
            message,
            request.getRequestURI(),
            traceId
        );
        return ResponseEntity.status(status).body(apiError);
    }

    @SuppressWarnings("null")
    private String resolve(String code, Locale locale) {
        String resolvedCode = Objects.requireNonNull(code);
        Locale resolvedLocale = Objects.requireNonNullElse(locale, Locale.getDefault());
        return messageSource.getMessage(resolvedCode, null, resolvedCode, resolvedLocale);
    }
}