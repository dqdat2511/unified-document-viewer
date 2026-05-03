package com.example.gateway.application.service;

import com.example.gateway.application.model.DownstreamRouteHealth;
import com.example.gateway.application.model.GatewayRouteSnapshot;
import com.example.gateway.application.port.out.GatewayRouteCatalogPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GatewayRouteInspectionService — Unit Tests")
class GatewayRouteInspectionServiceTest {

    @Mock private GatewayRouteCatalogPort gatewayRouteCatalogPort;
    @InjectMocks private GatewayRouteInspectionService sut;

    private static final GatewayRouteSnapshot ROUTE_SNAPSHOT = new GatewayRouteSnapshot(
        "auth-route", "http://localhost:8081", Map.of("domain", "auth"),
        List.of("/api/v1/auth/**"), List.of("Path=/api/v1/auth/**"), Collections.emptyList()
    );

    @Test
    @DisplayName("listRoutes should return all routes from catalog port")
    void shouldListAllRoutes() {
        when(gatewayRouteCatalogPort.listRoutes()).thenReturn(Flux.just(ROUTE_SNAPSHOT));

        StepVerifier.create(sut.listRoutes())
            .expectNext(ROUTE_SNAPSHOT)
            .verifyComplete();
    }

    @Test
    @DisplayName("listRoutes should return empty Flux when no routes configured")
    void shouldReturnEmptyFluxWhenNoRoutes() {
        when(gatewayRouteCatalogPort.listRoutes()).thenReturn(Flux.empty());

        StepVerifier.create(sut.listRoutes())
            .verifyComplete();
    }

    @Test
    @DisplayName("resolveRoute should return matching route from catalog port")
    void shouldResolveRouteForGivenPath() {
        String path = "/api/v1/auth/login";
        when(gatewayRouteCatalogPort.resolveRoute(path)).thenReturn(Mono.just(ROUTE_SNAPSHOT));

        StepVerifier.create(sut.resolveRoute(path))
            .expectNext(ROUTE_SNAPSHOT)
            .verifyComplete();

        verify(gatewayRouteCatalogPort).resolveRoute(path);
    }

    @Test
    @DisplayName("resolveRoute should return empty Mono when no route matches")
    void shouldReturnEmptyMonoWhenNoRouteMatches() {
        when(gatewayRouteCatalogPort.resolveRoute("/unknown")).thenReturn(Mono.empty());

        StepVerifier.create(sut.resolveRoute("/unknown"))
            .verifyComplete();
    }

    @Test
    @DisplayName("getRouteHealth should return health info from catalog port")
    void shouldReturnRouteHealthFromPort() {
        DownstreamRouteHealth health = new DownstreamRouteHealth(
            "auth-route", "http://localhost:8081", "http://localhost:8081/actuator/health", "UP", 200);
        when(gatewayRouteCatalogPort.getRouteHealth()).thenReturn(Flux.just(health));

        StepVerifier.create(sut.getRouteHealth())
            .expectNextMatches(h -> "auth-route".equals(h.getRouteId()) && "UP".equals(h.getStatus()))
            .verifyComplete();
    }
}
