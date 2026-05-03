package com.example.gateway.adapters.in.web.dto;

import java.util.List;
import java.util.Map;

public class GatewayRouteResponse {

    private String routeId;
    private String targetUri;
    private Map<String, Object> metadata;
    private List<String> pathPatterns;
    private List<String> predicates;
    private List<String> filters;

    public GatewayRouteResponse() {}

    public String getRouteId() { return routeId; }
    public String getTargetUri() { return targetUri; }
    public Map<String, Object> getMetadata() { return metadata; }
    public List<String> getPathPatterns() { return pathPatterns; }
    public List<String> getPredicates() { return predicates; }
    public List<String> getFilters() { return filters; }
    public void setRouteId(String routeId) { this.routeId = routeId; }
    public void setTargetUri(String targetUri) { this.targetUri = targetUri; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    public void setPathPatterns(List<String> pathPatterns) { this.pathPatterns = pathPatterns; }
    public void setPredicates(List<String> predicates) { this.predicates = predicates; }
    public void setFilters(List<String> filters) { this.filters = filters; }
}
