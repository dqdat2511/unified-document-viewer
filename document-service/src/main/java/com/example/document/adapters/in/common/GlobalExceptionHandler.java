package com.example.document.adapters.in.common;

import com.example.document.adapters.in.web.dto.ApiError;
import com.example.document.adapters.in.web.filter.TraceIdFilter;
import com.example.document.domain.exception.ApplicationErrorCode;
import com.example.document.domain.exception.DocumentRuntimeException;
import com.example.document.domain.exception.ErrorCodeProvider;
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
            .orElse(resolve(errorCode, request.getLocale()));
        return build(errorCode, detail, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        ApplicationErrorCode errorCode = errorCodeProvider.getConstraintError();
        String detail = resolve(errorCode, request.getLocale()) + ": " + ex.getMessage();
        return build(errorCode, detail, request);
    }

    @ExceptionHandler(DocumentRuntimeException.class)
    public ResponseEntity<ApiError> handleDocumentRuntimeException(DocumentRuntimeException ex, HttpServletRequest request, Locale locale) {
        ApplicationErrorCode errorCode = ex.getErrorCode();
        String message = messageSource.getMessage(errorCode.getMessageKey(), ex.getMessageArgs(), ex.getMessage(), locale);
        log.warn("[{}] {} {}", errorCode.getErrorCode(), request.getMethod(), request.getRequestURI(), ex);
        return build(errorCode, message, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception {} {}", request.getMethod(), request.getRequestURI(), ex);
        ApplicationErrorCode errorCode = errorCodeProvider.getInternalError();
        return build(errorCode, resolve(errorCode, request.getLocale()), request);
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
    private String resolve(ApplicationErrorCode errorCode, Locale locale) {
        String messageKey = Objects.requireNonNull(errorCode.getMessageKey());
        Locale resolvedLocale = Objects.requireNonNullElse(locale, Locale.getDefault());
        return messageSource.getMessage(messageKey, null, messageKey, resolvedLocale);
    }
}