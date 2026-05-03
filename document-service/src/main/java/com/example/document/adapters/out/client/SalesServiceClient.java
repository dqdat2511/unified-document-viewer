package com.example.document.adapters.out.client;

import com.example.document.adapters.out.client.dto.SalesDocumentDto;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client — Sales System API
 * External system that provides sales documents (mocked to localhost:8090 for Scenario D).
 */
@FeignClient(
    name = "salesServiceClient",
    url = "${external.sales.url:http://localhost:8090}"
)
public interface SalesServiceClient {

    @GetMapping("/api/vehicles/{vin}/documents")
    List<SalesDocumentDto> getDocuments(@PathVariable("vin") String vin);
}
