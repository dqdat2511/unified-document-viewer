package com.example.gateway.application.port.in;

import com.example.gateway.application.model.DownstreamRouteHealth;
import com.example.gateway.application.model.GatewayRouteSnapshot;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InspectGatewayRoutesUseCase {
    Flux<GatewayRouteSnapshot> listRoutes();

    Mono<GatewayRouteSnapshot> resolveRoute(String requestPath);

    Flux<DownstreamRouteHealth> getRouteHealth();
}
