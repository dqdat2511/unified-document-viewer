package com.example.gateway.adapters.in.web.filter;

import com.example.gateway.application.port.in.ValidateGatewayAccessUseCase;
import com.example.gateway.infrastructure.config.EndpointRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtValidationFilter implements GlobalFilter, Ordered {

    private final ValidateGatewayAccessUseCase validateGatewayAccessUseCase;
    private final EndpointRegistry endpointRegistry;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        if (endpointRegistry.isPublic(path)) {
            return chain.filter(exchange);
        }

        String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        return validateGatewayAccessUseCase.validateAuthorizationHeader(authorizationHeader)
            .flatMap(isValid -> {
                if (Boolean.TRUE.equals(isValid)) {
                    return chain.filter(exchange);
                }
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            });
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
