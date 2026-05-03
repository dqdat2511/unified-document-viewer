package com.example.document.application;

import com.example.document.application.port.out.CachePort;
import com.example.document.application.port.out.DocumentRepositoryPort;
import com.example.document.application.service.DocumentAggregationService;
import com.example.document.application.service.DocumentFetchService;
import com.example.document.domain.ActiveVin;
import com.example.document.domain.AggregatedDocuments;
import com.example.document.domain.DocumentItem;
import com.example.document.domain.exception.DocumentRuntimeException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DocumentAggregationService — Unit Tests")
class DocumentAggregationServiceTest {

    @Mock private DocumentFetchService documentFetchService;
    @Mock private CachePort<DocumentItem> cachePort;
    @Mock private DocumentRepositoryPort documentRepositoryPort;

    private DocumentAggregationService sut;

    private static final String VIN = "1HGBH41JXMN109186";

    @BeforeEach
    void setUp() {
        sut = new DocumentAggregationService(
            documentFetchService, cachePort, documentRepositoryPort,
            new SimpleMeterRegistry()
        );
    }

    @Test
    @DisplayName("Should return cached documents on Redis cache hit")
    void shouldReturnCachedDocumentsOnCacheHit() {
        List<DocumentItem> cached = List.of(makeDoc("D1", "SALES"));
        when(cachePort.getCachedEntities(eq(VIN), any(TypeReference.class))).thenReturn(cached);

        AggregatedDocuments result = sut.aggregateByVin(VIN);

        assertThat(result.getVin()).isEqualTo(VIN);
        assertThat(result.getDocuments()).hasSize(1);
        verifyNoInteractions(documentFetchService);
    }

    @Test
    @DisplayName("Should fetch from external APIs on cache miss and active VIN found")
    void shouldFetchFromExternalOnCacheMiss() {
        List<DocumentItem> fetched = List.of(makeDoc("D1", "SALES"), makeDoc("D2", "SERVICE"));
        when(cachePort.getCachedEntities(eq(VIN), any(TypeReference.class))).thenReturn(Collections.emptyList());
        when(documentRepositoryPort.findByVin(VIN)).thenReturn(Optional.of(new ActiveVin(VIN, "ACTIVE")));
        when(documentFetchService.fetchAll(VIN)).thenReturn(fetched);

        AggregatedDocuments result = sut.aggregateByVin(VIN);

        assertThat(result.getDocuments()).hasSize(2);
        verify(cachePort).cacheEntities(fetched, VIN);
    }

    @Test
    @DisplayName("Should throw DocumentRuntimeException when VIN not active in DB")
    void shouldThrowWhenVinNotFound() {
        when(cachePort.getCachedEntities(eq(VIN), any(TypeReference.class))).thenReturn(Collections.emptyList());
        when(documentRepositoryPort.findByVin(VIN)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.aggregateByVin(VIN))
            .isInstanceOf(DocumentRuntimeException.class);

        verifyNoInteractions(documentFetchService);
    }

    @Test
    @DisplayName("Should persist results to cache after successful external fetch")
    void shouldCacheResultsAfterFetch() {
        List<DocumentItem> fetched = List.of(makeDoc("D1", "SALES"));
        when(cachePort.getCachedEntities(eq(VIN), any(TypeReference.class))).thenReturn(Collections.emptyList());
        when(documentRepositoryPort.findByVin(VIN)).thenReturn(Optional.of(new ActiveVin(VIN, "ACTIVE")));
        when(documentFetchService.fetchAll(VIN)).thenReturn(fetched);

        sut.aggregateByVin(VIN);

        verify(cachePort).cacheEntities(eq(fetched), eq(VIN));
    }

    @Test
    @DisplayName("Should return empty documents list when external APIs return nothing")
    void shouldReturnEmptyWhenNoDocuments() {
        when(cachePort.getCachedEntities(eq(VIN), any(TypeReference.class))).thenReturn(Collections.emptyList());
        when(documentRepositoryPort.findByVin(VIN)).thenReturn(Optional.of(new ActiveVin(VIN, "ACTIVE")));
        when(documentFetchService.fetchAll(VIN)).thenReturn(Collections.emptyList());

        AggregatedDocuments result = sut.aggregateByVin(VIN);

        assertThat(result.getDocuments()).isEmpty();
    }

    private DocumentItem makeDoc(String id, String source) {
        return new DocumentItem(id, VIN, "Test Doc " + id, "PDF", "https://example.com/" + id, "2024-01-01T00:00:00Z", source);
    }
}
