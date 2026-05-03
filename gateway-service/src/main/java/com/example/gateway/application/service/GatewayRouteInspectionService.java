package com.example.gateway.application.service;

import com.example.gateway.application.model.DownstreamRouteHealth;
import com.example.gateway.application.model.GatewayRouteSnapshot;
import com.example.gateway.application.port.in.InspectGatewayRoutesUseCase;
import com.example.gateway.application.port.out.GatewayRouteCatalogPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GatewayRouteInspectionService implements InspectGatewayRoutesUseCase {

    private final GatewayRouteCatalogPort gatewayRouteCatalogPort;

    @Override
    public Flux<GatewayRouteSnapshot> listRoutes() {
        return gatewayRouteCatalogPort.listRoutes();
    }

    @Override
    public Mono<GatewayRouteSnapshot> resolveRoute(String requestPath) {
        return gatewayRouteCatalogPort.resolveRoute(requestPath);
    }

    @Override
    public Flux<DownstreamRouteHealth> getRouteHealth() {
        return gatewayRouteCatalogPort.getRouteHealth();
    }
}
