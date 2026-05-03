package com.example.document.domain.exception;

public interface ErrorCodeProvider {
    ApplicationErrorCode getValidationError();
    ApplicationErrorCode getConstraintError();
    ApplicationErrorCode getInternalError();
}
