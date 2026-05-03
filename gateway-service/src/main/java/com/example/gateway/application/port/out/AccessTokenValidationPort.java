package com.example.gateway.application.port.out;

import reactor.core.publisher.Mono;

public interface AccessTokenValidationPort {
    Mono<Boolean> validateAuthorizationHeader(String authorizationHeader);
}
