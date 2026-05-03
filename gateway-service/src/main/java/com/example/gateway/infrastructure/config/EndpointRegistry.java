package com.example.gateway.infrastructure.config;

import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class EndpointRegistry {

    private static final Set<String> PUBLIC_PATHS = Set.of(
        "/api/v1/auth/login",
        "/api/v1/auth/register",
        "/actuator/"
    );

    public boolean isPublic(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }
}
