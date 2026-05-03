package com.example.document.adapter;

import com.example.document.adapters.out.client.SalesDocumentProviderAdapter;
import com.example.document.adapters.out.client.SalesServiceClient;
import com.example.document.adapters.out.client.dto.SalesDocumentDto;
import com.example.document.domain.DocumentItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SalesDocumentProviderAdapter — Unit Tests")
class SalesDocumentProviderAdapterTest {

    @Mock private SalesServiceClient salesServiceClient;
    @InjectMocks private SalesDocumentProviderAdapter sut;

    private static final String VIN = "1HGBH41JXMN109186";

    @Test
    @DisplayName("Should map SalesDocumentDto to DocumentItem with source=SALES")
    void shouldMapToDocumentItemWithSalesSource() {
        var dto = new SalesDocumentDto(
            "S1", VIN, "Invoice", "PDF", "https://example.com/s1", "2024-01-01T00:00:00Z");
        when(salesServiceClient.getDocuments(VIN)).thenReturn(List.of(dto));

        List<DocumentItem> result = sut.fetchDocuments(VIN);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSource()).isEqualTo("SALES");
        assertThat(result.get(0).getId()).isEqualTo("S1");
        assertThat(result.get(0).getName()).isEqualTo("Invoice");
    }

    @Test
    @DisplayName("Should return empty list on client exception (fault isolation)")
    void shouldReturnEmptyOnClientException() {
        when(salesServiceClient.getDocuments(VIN)).thenThrow(new RuntimeException("Network error"));

        List<DocumentItem> result = sut.fetchDocuments(VIN);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("providerName should return SALES")
    void shouldReturnCorrectProviderName() {
        assertThat(sut.providerName()).isEqualTo("SALES");
    }
}
