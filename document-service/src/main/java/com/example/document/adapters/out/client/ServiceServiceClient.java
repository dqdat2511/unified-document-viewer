package com.example.document.adapters.out.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client — Service System API
 * External system that provides service documents (mocked to localhost:8091 for Scenario D).
 */
@FeignClient(
    name = "serviceServiceClient",
    url = "${external.service.url:http://localhost:8091}"
)
public interface ServiceServiceClient {

    @GetMapping("/api/vehicles/{vin}/documents")
    List<ServiceDocumentDto> getDocuments(@PathVariable("vin") String vin);

    record ServiceDocumentDto(
        String id,
        String vin,
        String name,
        String type,
        String url,
        @JsonProperty("created_at") String createdAt
    ) {}
}
