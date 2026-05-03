package com.example.document.adapter;

import com.example.document.adapters.out.client.ServiceDocumentProviderAdapter;
import com.example.document.adapters.out.client.ServiceServiceClient;
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
@DisplayName("ServiceDocumentProviderAdapter — Unit Tests")
class ServiceDocumentProviderAdapterTest {

    @Mock private ServiceServiceClient serviceServiceClient;
    @InjectMocks private ServiceDocumentProviderAdapter sut;

    private static final String VIN = "1HGBH41JXMN109186";

    @Test
    @DisplayName("Should map ServiceDocumentDto to DocumentItem with source=SERVICE")
    void shouldMapToDocumentItemWithServiceSource() {
        var dto = new ServiceServiceClient.ServiceDocumentDto(
            "SVC1", VIN, "Maintenance Report", "PDF", "https://example.com/svc1", "2024-06-01T00:00:00Z");
        when(serviceServiceClient.getDocuments(VIN)).thenReturn(List.of(dto));

        List<DocumentItem> result = sut.fetchDocuments(VIN);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSource()).isEqualTo("SERVICE");
        assertThat(result.get(0).getId()).isEqualTo("SVC1");
        assertThat(result.get(0).getName()).isEqualTo("Maintenance Report");
        assertThat(result.get(0).getVin()).isEqualTo(VIN);
    }

    @Test
    @DisplayName("Should map all fields correctly from dto")
    void shouldMapAllFieldsFromDto() {
        var dto = new ServiceServiceClient.ServiceDocumentDto(
            "SVC2", VIN, "Oil Change", "JPEG", "https://example.com/svc2", "2024-08-15");
        when(serviceServiceClient.getDocuments(VIN)).thenReturn(List.of(dto));

        DocumentItem result = sut.fetchDocuments(VIN).get(0);

        assertThat(result.getId()).isEqualTo("SVC2");
        assertThat(result.getVin()).isEqualTo(VIN);
        assertThat(result.getName()).isEqualTo("Oil Change");
        assertThat(result.getType()).isEqualTo("JPEG");
        assertThat(result.getUrl()).isEqualTo("https://example.com/svc2");
        assertThat(result.getCreatedAt()).isEqualTo("2024-08-15");
        assertThat(result.getSource()).isEqualTo("SERVICE");
    }

    @Test
    @DisplayName("Should return empty list on client exception (fault isolation)")
    void shouldReturnEmptyOnClientException() {
        when(serviceServiceClient.getDocuments(VIN)).thenThrow(new RuntimeException("Service API down"));

        List<DocumentItem> result = sut.fetchDocuments(VIN);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return empty list when client returns empty list")
    void shouldReturnEmptyWhenClientReturnsEmpty() {
        when(serviceServiceClient.getDocuments(VIN)).thenReturn(List.of());

        List<DocumentItem> result = sut.fetchDocuments(VIN);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("providerName should return SERVICE")
    void shouldReturnCorrectProviderName() {
        assertThat(sut.providerName()).isEqualTo("SERVICE");
    }
}
