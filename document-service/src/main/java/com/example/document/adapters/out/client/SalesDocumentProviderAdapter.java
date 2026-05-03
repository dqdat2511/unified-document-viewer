package com.example.document.adapters.out.client;

import com.example.document.application.port.out.DocumentProviderPort;
import com.example.document.domain.DocumentItem;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * SECONDARY ADAPTER — Sales System.
 * Implements DocumentProviderPort; translates external DTO → domain model.
 *
 * FIX: This is now the ONLY place that knows about SalesServiceClient (Feign).
 * The application layer (DocumentFetchService) only sees DocumentProviderPort.
 */
@Component
@RequiredArgsConstructor
public class SalesDocumentProviderAdapter implements DocumentProviderPort {

    private static final Logger log = LoggerFactory.getLogger(SalesDocumentProviderAdapter.class);

    private final SalesServiceClient salesServiceClient;

    @Override
    public List<DocumentItem> fetchDocuments(String vin) {
        try {
            return salesServiceClient.getDocuments(vin).stream()
                .map(dto -> new DocumentItem(
                    dto.id(), dto.vin(), dto.name(),
                    dto.type(), dto.url(), dto.createdAt(), "SALES"))
                .toList();
        } catch (Exception e) {
            log.warn("[SALES] Failed to fetch documents for vin={}: {}", vin, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public String providerName() {
        return "SALES";
    }
}
