package com.example.gateway.application.service;

import com.example.gateway.application.port.in.ValidateGatewayAccessUseCase;
import com.example.gateway.application.port.out.AccessTokenValidationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GatewayAccessValidationService implements ValidateGatewayAccessUseCase {

    private final AccessTokenValidationPort accessTokenValidationPort;

    @Override
    public Mono<Boolean> validateAuthorizationHeader(String authorizationHeader) {
        return accessTokenValidationPort.validateAuthorizationHeader(authorizationHeader);
    }
}
