package com.example.gateway.application.port.in;

import reactor.core.publisher.Mono;

public interface ValidateGatewayAccessUseCase {
    Mono<Boolean> validateAuthorizationHeader(String authorizationHeader);
}
