package com.example.gateway.adapters.out.security;

import com.example.gateway.application.port.out.AccessTokenValidationPort;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AuthServiceTokenValidationAdapter implements AccessTokenValidationPort {

    private final WebClient webClient;
    private final String authValidateUrl;

    public AuthServiceTokenValidationAdapter(WebClient.Builder builder,
                                             @Value("${auth.validate.url}") String authValidateUrl) {
        this.webClient = builder.build();
        this.authValidateUrl = authValidateUrl;
    }

    @Override
    public Mono<Boolean> validateAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return Mono.just(false);
        }

        return webClient.post()
            .uri(authValidateUrl)
            .header("Authorization", authorizationHeader)
            .retrieve()
            .toBodilessEntity()
            .timeout(Duration.ofSeconds(3))
            .map(response -> true)
            .onErrorResume(ex -> Mono.just(false));
    }
}
