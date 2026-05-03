package com.example.document.domain.exception;

import org.springframework.stereotype.Component;

@Component
public class DocumentErrorCodeProvider implements ErrorCodeProvider {

    @Override
    public ApplicationErrorCode getValidationError() {
        return DocumentErrorCode.VALIDATION;
    }

    @Override
    public ApplicationErrorCode getConstraintError() {
        return DocumentErrorCode.CONSTRAINT;
    }

    @Override
    public ApplicationErrorCode getInternalError() {
        return DocumentErrorCode.INTERNAL;
    }
}
