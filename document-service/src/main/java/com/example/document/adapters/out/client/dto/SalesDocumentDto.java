package com.example.document.adapters.out.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SalesDocumentDto(
    String id,
    String vin,
    String name,
    String type,
    String url,
    @JsonProperty("created_at") String createdAt
) {}
