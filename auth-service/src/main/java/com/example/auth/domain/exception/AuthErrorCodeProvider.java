package com.example.auth.domain.exception;

import org.springframework.stereotype.Component;

@Component
public class AuthErrorCodeProvider implements ErrorCodeProvider {

    @Override
    public ApplicationErrorCode getValidationError() {
        return AuthErrorCode.VALIDATION;
    }

    @Override
    public ApplicationErrorCode getConstraintError() {
        return AuthErrorCode.CONSTRAINT;
    }

    @Override
    public ApplicationErrorCode getInternalError() {
        return AuthErrorCode.INTERNAL;
    }
}
