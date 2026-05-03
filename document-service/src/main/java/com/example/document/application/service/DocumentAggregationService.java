package com.example.document.application.service;

import com.example.document.application.port.in.AggregateDocumentsUseCase;
import com.example.document.application.port.out.CachePort;
import com.example.document.application.port.out.DocumentRepositoryPort;
import com.example.document.domain.ActiveVin;
import com.example.document.domain.AggregatedDocuments;
import com.example.document.domain.DocumentItem;
import com.example.document.domain.exception.DocumentErrorCode;
import com.example.document.domain.exception.DocumentRuntimeException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class DocumentAggregationService implements AggregateDocumentsUseCase {

    private static final Logger log = LoggerFactory.getLogger(DocumentAggregationService.class);

    private final DocumentFetchService documentFetchService;
    private final CachePort<DocumentItem> documentItemCachePort; 
    private final DocumentRepositoryPort documentRepositoryPort;
    private final MeterRegistry meterRegistry;

    @Override
    public AggregatedDocuments aggregateByVin(String vin) {
        log.info("Aggregating documents for vin={}", vin);
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            return doAggregate(vin);
        } finally {
            sample.stop(Timer.builder("documents.aggregation.latency")
                .description("End-to-end latency of document aggregation")
                .register(meterRegistry));
        }
    }

    private AggregatedDocuments doAggregate(String vin) {
        // 1. Redis cache hit
        List<DocumentItem> cached = documentItemCachePort.getCachedEntities(
            vin, new TypeReference<List<DocumentItem>>() {});
        if (!CollectionUtils.isEmpty(cached)) {
            log.info("Redis cache hit for vin={}, count={}", vin, cached.size());
            meterRegistry.counter("documents.cache.hit", "layer", "redis").increment();
            return new AggregatedDocuments(vin, cached);
        }

        log.info("Redis cache miss for vin={}", vin);
        meterRegistry.counter("documents.cache.miss", "layer", "redis").increment();

        // 2. Validate active VIN from DB
        Optional<ActiveVin> activeVin = documentRepositoryPort.findByVin(vin);

        return activeVin.map(v -> {
            List<DocumentItem> fetched = documentFetchService.fetchAll(v.getVin());
            documentItemCachePort.cacheEntities(fetched, vin);
            log.info("Fetched {} document(s) from external APIs for vin={}", fetched.size(), vin);
            return new AggregatedDocuments(vin, fetched);
        }).orElseThrow(() ->
            new DocumentRuntimeException(
                DocumentErrorCode.DOCUMENT_NOT_FOUND,
                "No active VIN found in repository for vin=%s", vin)
        );
    }
}
