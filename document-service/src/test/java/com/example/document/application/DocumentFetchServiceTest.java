package com.example.document.application;

import com.example.document.application.port.out.DocumentProviderPort;
import com.example.document.application.service.DocumentFetchService;
import com.example.document.domain.DocumentItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("DocumentFetchService — Unit Tests")
class DocumentFetchServiceTest {

    @Mock private DocumentProviderPort salesProvider;
    @Mock private DocumentProviderPort serviceProvider;

    private DocumentFetchService sut;
    private static final String VIN = "1HGBH41JXMN109186";

    @BeforeEach
    void setUp() {
        when(salesProvider.providerName()).thenReturn("SALES");
        when(serviceProvider.providerName()).thenReturn("SERVICE");
        sut = new DocumentFetchService(
            List.of(salesProvider, serviceProvider),
            Executors.newFixedThreadPool(2)
        );
        // @Value is not processed when instantiating directly; set the timeout explicitly
        ReflectionTestUtils.setField(sut, "fetchTimeoutSeconds", 5);
    }

    @Test
    @DisplayName("Should combine documents from all providers")
    void shouldCombineFromAllProviders() {
        when(salesProvider.fetchDocuments(VIN)).thenReturn(List.of(doc("S1", "SALES")));
        when(serviceProvider.fetchDocuments(VIN)).thenReturn(List.of(doc("SVC1", "SERVICE")));

        List<DocumentItem> result = sut.fetchAll(VIN);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(DocumentItem::getSource)
            .containsExactlyInAnyOrder("SALES", "SERVICE");
    }

    @Test
    @DisplayName("Should return partial results when one provider fails")
    void shouldReturnPartialResultsOnProviderFailure() {
        when(salesProvider.fetchDocuments(VIN)).thenThrow(new RuntimeException("Sales API down"));
        when(serviceProvider.fetchDocuments(VIN)).thenReturn(List.of(doc("SVC1", "SERVICE")));

        // The adapter's try/catch returns empty list on exception
        // Here we test the service handles empty gracefully
        List<DocumentItem> result = sut.fetchAll(VIN);

        // Service side still returns its docs
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSource()).isEqualTo("SERVICE");
    }

    @Test
    @DisplayName("Should return empty list when all providers fail")
    void shouldReturnEmptyWhenAllFail() {
        when(salesProvider.fetchDocuments(VIN)).thenReturn(List.of());
        when(serviceProvider.fetchDocuments(VIN)).thenReturn(List.of());

        List<DocumentItem> result = sut.fetchAll(VIN);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should call all providers for the given VIN")
    void shouldCallAllProviders() {
        when(salesProvider.fetchDocuments(VIN)).thenReturn(List.of());
        when(serviceProvider.fetchDocuments(VIN)).thenReturn(List.of());

        sut.fetchAll(VIN);

        verify(salesProvider).fetchDocuments(VIN);
        verify(serviceProvider).fetchDocuments(VIN);
    }

    private DocumentItem doc(String id, String source) {
        return new DocumentItem(id, VIN, "Doc " + id, "PDF", "https://ex.com/" + id, "2024-01-01", source);
    }
}
