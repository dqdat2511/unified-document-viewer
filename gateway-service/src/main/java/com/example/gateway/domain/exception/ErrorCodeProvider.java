package com.example.gateway.domain.exception;

public interface ErrorCodeProvider {
    ApplicationErrorCode getValidationError();
    ApplicationErrorCode getConstraintError();
    ApplicationErrorCode getInternalError();
}
