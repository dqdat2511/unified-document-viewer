package com.example.auth.domain.exception;

public interface ErrorCodeProvider {
    ApplicationErrorCode getValidationError();
    ApplicationErrorCode getConstraintError();
    ApplicationErrorCode getInternalError();
}
