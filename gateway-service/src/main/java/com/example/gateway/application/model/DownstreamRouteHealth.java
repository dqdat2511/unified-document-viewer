package com.example.gateway.application.model;

public class DownstreamRouteHealth {

    private final String routeId;
    private final String targetUri;
    private final String healthUri;
    private final String status;
    private final int httpStatus;

    public DownstreamRouteHealth(String routeId, String targetUri, String healthUri, String status, int httpStatus) {
        this.routeId = routeId;
        this.targetUri = targetUri;
        this.healthUri = healthUri;
        this.status = status;
        this.httpStatus = httpStatus;
    }

    public String getRouteId() { return routeId; }
    public String getTargetUri() { return targetUri; }
    public String getHealthUri() { return healthUri; }
    public String getStatus() { return status; }
    public int getHttpStatus() { return httpStatus; }
}
