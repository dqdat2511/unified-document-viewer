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
 * SECONDARY ADAPTER — Service System.
 * Implements DocumentProviderPort; translates external DTO → domain model.
 */
@Component
@RequiredArgsConstructor
public class ServiceDocumentProviderAdapter implements DocumentProviderPort {

    private static final Logger log = LoggerFactory.getLogger(ServiceDocumentProviderAdapter.class);

    private final ServiceServiceClient serviceServiceClient;

    @Override
    public List<DocumentItem> fetchDocuments(String vin) {
        try {
            return serviceServiceClient.getDocuments(vin).stream()
                .map(dto -> new DocumentItem(
                    dto.id(), dto.vin(), dto.name(),
                    dto.type(), dto.url(), dto.createdAt(), "SERVICE"))
                .toList();
        } catch (Exception e) {
            log.warn("[SERVICE] Failed to fetch documents for vin={}: {}", vin, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public String providerName() {
        return "SERVICE";
    }
}
