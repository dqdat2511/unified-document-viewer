package com.example.gateway.adapters.in.web;

import com.example.gateway.adapters.in.web.converter.GatewayRouteResponseConverter;
import com.example.gateway.application.port.in.InspectGatewayRoutesUseCase;
import com.example.gateway.adapters.in.web.dto.GatewayRouteHealthResponse;
import com.example.gateway.adapters.in.web.dto.GatewayRouteResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/gateway")
@RequiredArgsConstructor
public class GatewayRouteController {

    private final InspectGatewayRoutesUseCase inspectGatewayRoutesUseCase;
    private final GatewayRouteResponseConverter gatewayRouteResponseConverter;

    @GetMapping("/routes")
    public Mono<List<GatewayRouteResponse>> listRoutes() {
        return inspectGatewayRoutesUseCase.listRoutes().collectList()
            .flatMap(routes -> Mono.just(gatewayRouteResponseConverter.toRouteResponses(routes)));
    }

    @GetMapping("/routes/resolve")
    public Mono<ResponseEntity<GatewayRouteResponse>> resolveRoute(@RequestParam("path") String path) {
        return inspectGatewayRoutesUseCase.resolveRoute(path)
            .flatMap(route -> Mono.just(gatewayRouteResponseConverter.toRouteResponse(route)))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/routes/health")
    public Mono<List<GatewayRouteHealthResponse>> routeHealth() {
        return inspectGatewayRoutesUseCase.getRouteHealth().collectList()
            .flatMap(healths -> Mono.just(gatewayRouteResponseConverter.toHealthResponses(healths)));
    }
}
