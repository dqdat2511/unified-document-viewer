package com.example.gateway.application.model;

import java.util.List;
import java.util.Map;

public class GatewayRouteSnapshot {

    private final String routeId;
    private final String targetUri;
    private final Map<String, Object> metadata;
    private final List<String> pathPatterns;
    private final List<String> predicates;
    private final List<String> filters;

    public GatewayRouteSnapshot(String routeId, String targetUri, Map<String, Object> metadata,
                                 List<String> pathPatterns, List<String> predicates, List<String> filters) {
        this.routeId = routeId;
        this.targetUri = targetUri;
        this.metadata = metadata;
        this.pathPatterns = pathPatterns;
        this.predicates = predicates;
        this.filters = filters;
    }

    public String getRouteId() { return routeId; }
    public String getTargetUri() { return targetUri; }
    public Map<String, Object> getMetadata() { return metadata; }
    public List<String> getPathPatterns() { return pathPatterns; }
    public List<String> getPredicates() { return predicates; }
    public List<String> getFilters() { return filters; }
}
