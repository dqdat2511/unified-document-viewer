package com.example.gateway.domain.exception;

import org.springframework.stereotype.Component;

@Component
public class GatewayErrorCodeProvider implements ErrorCodeProvider {

    @Override
    public ApplicationErrorCode getValidationError() {
        return GatewayErrorCode.INTERNAL;
    }

    @Override
    public ApplicationErrorCode getConstraintError() {
        return GatewayErrorCode.INTERNAL;
    }

    @Override
    public ApplicationErrorCode getInternalError() {
        return GatewayErrorCode.INTERNAL;
    }
}
