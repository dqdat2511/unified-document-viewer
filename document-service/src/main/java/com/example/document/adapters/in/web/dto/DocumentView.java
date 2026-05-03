package com.example.document.adapters.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DocumentView {

    private String id;
    private String vin;
    private String name;
    private String type;
    private String url;
    private String createdAt;
    private String source;

    public DocumentView() {}

    public String getId() { return id; }
    public String getVin() { return vin; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getUrl() { return url; }
    @JsonProperty("created_at")
    public String getCreatedAt() { return createdAt; }
    public String getSource() { return source; }

    public void setId(String id) { this.id = id; }
    public void setVin(String vin) { this.vin = vin; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setUrl(String url) { this.url = url; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setSource(String source) { this.source = source; }
}