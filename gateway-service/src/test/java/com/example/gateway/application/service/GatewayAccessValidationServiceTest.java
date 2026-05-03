package com.example.gateway.application.service;

import com.example.gateway.application.port.out.AccessTokenValidationPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GatewayAccessValidationService — Unit Tests")
class GatewayAccessValidationServiceTest {

    @Mock private AccessTokenValidationPort accessTokenValidationPort;
    @InjectMocks private GatewayAccessValidationService sut;

    private static final String VALID_HEADER = "Bearer valid.jwt.token";
    private static final String INVALID_HEADER = "Bearer invalid.jwt";

    @Test
    @DisplayName("Should return true when token validation port returns true")
    void shouldReturnTrueForValidToken() {
        when(accessTokenValidationPort.validateAuthorizationHeader(VALID_HEADER))
            .thenReturn(Mono.just(true));

        StepVerifier.create(sut.validateAuthorizationHeader(VALID_HEADER))
            .expectNext(true)
            .verifyComplete();
    }

    @Test
    @DisplayName("Should return false when token validation port returns false")
    void shouldReturnFalseForInvalidToken() {
        when(accessTokenValidationPort.validateAuthorizationHeader(INVALID_HEADER))
            .thenReturn(Mono.just(false));

        StepVerifier.create(sut.validateAuthorizationHeader(INVALID_HEADER))
            .expectNext(false)
            .verifyComplete();
    }

    @Test
    @DisplayName("Should propagate error from token validation port")
    void shouldPropagatePortError() {
        when(accessTokenValidationPort.validateAuthorizationHeader(VALID_HEADER))
            .thenReturn(Mono.error(new RuntimeException("Auth service unreachable")));

        StepVerifier.create(sut.validateAuthorizationHeader(VALID_HEADER))
            .expectErrorMessage("Auth service unreachable")
            .verify();
    }

    @Test
    @DisplayName("Should delegate to port with the exact header value")
    void shouldDelegateToPortWithExactHeader() {
        when(accessTokenValidationPort.validateAuthorizationHeader(VALID_HEADER))
            .thenReturn(Mono.just(true));

        sut.validateAuthorizationHeader(VALID_HEADER).block();

        verify(accessTokenValidationPort).validateAuthorizationHeader(VALID_HEADER);
    }
}
