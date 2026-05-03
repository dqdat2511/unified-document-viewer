package com.example.gateway.adapters.in.web.converter;

import com.example.gateway.application.model.DownstreamRouteHealth;
import com.example.gateway.application.model.GatewayRouteSnapshot;
import com.example.gateway.adapters.in.web.dto.GatewayRouteHealthResponse;
import com.example.gateway.adapters.in.web.dto.GatewayRouteResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GatewayRouteResponseConverter {
    private final ModelMapper modelMapper;

    public GatewayRouteResponse toRouteResponse(GatewayRouteSnapshot routeSnapshot) {
        return modelMapper.map(routeSnapshot, GatewayRouteResponse.class);
    }

    public List<GatewayRouteResponse> toRouteResponses(List<GatewayRouteSnapshot> routeSnapshots) {
        return routeSnapshots.stream()
            .map(this::toRouteResponse)
            .toList();
    }

    public GatewayRouteHealthResponse toHealthResponse(DownstreamRouteHealth downstreamRouteHealth) {
        return modelMapper.map(downstreamRouteHealth, GatewayRouteHealthResponse.class);
    }

    public List<GatewayRouteHealthResponse> toHealthResponses(List<DownstreamRouteHealth> downstreamRouteHealths) {
        return downstreamRouteHealths.stream()
            .map(this::toHealthResponse)
            .toList();
    }
}