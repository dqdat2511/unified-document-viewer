package com.example.document.application.port.in;

import com.example.document.domain.AggregatedDocuments;

/**
 * Inbound Port (Hexagonal Architecture)
 * Use case interface exposed to external adapters (controllers, CLI, etc.).
 * Clients invoke this use case to aggregate documents by VIN.
 */
public interface AggregateDocumentsUseCase {
    AggregatedDocuments aggregateByVin(String vin);
}
