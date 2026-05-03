package com.example.gateway.adapters.in.web.dto;

public class GatewayRouteHealthResponse {

    private String routeId;
    private String targetUri;
    private String healthUri;
    private String status;
    private int httpStatus;

    public GatewayRouteHealthResponse() {}

    public String getRouteId() { return routeId; }
    public String getTargetUri() { return targetUri; }
    public String getHealthUri() { return healthUri; }
    public String getStatus() { return status; }
    public int getHttpStatus() { return httpStatus; }
    public void setRouteId(String routeId) { this.routeId = routeId; }
    public void setTargetUri(String targetUri) { this.targetUri = targetUri; }
    public void setHealthUri(String healthUri) { this.healthUri = healthUri; }
    public void setStatus(String status) { this.status = status; }
    public void setHttpStatus(int httpStatus) { this.httpStatus = httpStatus; }
}
