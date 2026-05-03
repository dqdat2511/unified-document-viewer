package com.example.gateway.adapters.out.routing;

import com.example.gateway.application.model.DownstreamRouteHealth;
import com.example.gateway.application.model.GatewayRouteSnapshot;
import com.example.gateway.application.port.out.GatewayRouteCatalogPort;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class SpringCloudGatewayRouteCatalogAdapter implements GatewayRouteCatalogPort {

    private static final PathPatternParser PATH_PATTERN_PARSER = new PathPatternParser();

    private final RouteDefinitionLocator routeDefinitionLocator;
    private final WebClient webClient;

    public SpringCloudGatewayRouteCatalogAdapter(RouteDefinitionLocator routeDefinitionLocator,
                                                 WebClient.Builder webClientBuilder) {
        this.routeDefinitionLocator = routeDefinitionLocator;
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Flux<GatewayRouteSnapshot> listRoutes() {
        return routeDefinitionLocator.getRouteDefinitions().map(this::toSnapshot);
    }

    @Override
    public Mono<GatewayRouteSnapshot> resolveRoute(String requestPath) {
        return routeDefinitionLocator.getRouteDefinitions()
            .filter(routeDefinition -> matches(routeDefinition, requestPath))
            .next()
            .map(this::toSnapshot);
    }

    @Override
    public Flux<DownstreamRouteHealth> getRouteHealth() {
        return routeDefinitionLocator.getRouteDefinitions().flatMap(this::toHealth);
    }

    private GatewayRouteSnapshot toSnapshot(RouteDefinition routeDefinition) {
        return new GatewayRouteSnapshot(
            routeDefinition.getId(),
            routeDefinition.getUri().toString(),
            routeDefinition.getMetadata(),
            extractPathPatterns(routeDefinition),
            routeDefinition.getPredicates().stream().map(predicate -> predicate.getName() + predicate.getArgs()).toList(),
            routeDefinition.getFilters().stream().map(filter -> filter.getName() + filter.getArgs()).toList()
        );
    }

    private Mono<DownstreamRouteHealth> toHealth(RouteDefinition routeDefinition) {
        String healthUri = buildHealthUri(routeDefinition.getUri()).toString();
        return webClient.get()
            .uri(healthUri)
            .exchangeToMono(response -> Mono.just(new DownstreamRouteHealth(
                routeDefinition.getId(),
                routeDefinition.getUri().toString(),
                healthUri,
                response.statusCode().is2xxSuccessful() ? "UP" : "DOWN",
                response.statusCode().value()
            )))
            .timeout(Duration.ofSeconds(2))
            .onErrorResume(ex -> Mono.just(new DownstreamRouteHealth(
                routeDefinition.getId(),
                routeDefinition.getUri().toString(),
                healthUri,
                "DOWN",
                503
            )));
    }

    private boolean matches(RouteDefinition routeDefinition, String requestPath) {
        return extractPathPatterns(routeDefinition).stream().anyMatch(pattern -> {
            PathPattern pathPattern = PATH_PATTERN_PARSER.parse(pattern);
            return pathPattern.matches(PathContainer.parsePath(requestPath));
        });
    }

    private List<String> extractPathPatterns(RouteDefinition routeDefinition) {
        return routeDefinition.getPredicates().stream()
            .filter(predicate -> "Path".equalsIgnoreCase(predicate.getName()))
            .flatMap(predicate -> predicate.getArgs().entrySet().stream())
            .sorted(Map.Entry.comparingByKey())
            .map(Map.Entry::getValue)
            .toList();
    }

    private URI buildHealthUri(URI baseUri) {
        String separator = baseUri.toString().endsWith("/") ? "" : "/";
        return URI.create(baseUri + separator + "actuator/health");
    }
}
