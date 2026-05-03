package com.example.document.adapter;

import com.example.document.adapters.in.web.converter.DocumentResponseConverter;
import com.example.document.adapters.in.web.dto.DocumentResponse;
import com.example.document.domain.AggregatedDocuments;
import com.example.document.domain.DocumentItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DocumentResponseConverter — Unit Tests")
class DocumentResponseConverterTest {

    private DocumentResponseConverter sut;

    private static final String VIN = "1HGBH41JXMN109186";

    @BeforeEach
    void setUp() {
        // Use a real ModelMapper so field mapping is tested end-to-end
        sut = new DocumentResponseConverter(new ModelMapper());
    }

    @Test
    @DisplayName("Should map vin and total count from AggregatedDocuments")
    void shouldMapVinAndTotalCount() {
        DocumentItem item = new DocumentItem("D1", VIN, "Invoice", "PDF", "https://ex.com/d1", "2024-01-01", "SALES");
        AggregatedDocuments aggregated = new AggregatedDocuments(VIN, List.of(item));

        DocumentResponse response = sut.toResponse(aggregated);

        assertThat(response.getVin()).isEqualTo(VIN);
        assertThat(response.getTotal()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should map all document fields to DocumentView")
    void shouldMapDocumentItemFields() {
        DocumentItem item = new DocumentItem("D1", VIN, "Invoice", "PDF", "https://ex.com/d1", "2024-01-01", "SALES");
        AggregatedDocuments aggregated = new AggregatedDocuments(VIN, List.of(item));

        DocumentResponse response = sut.toResponse(aggregated);

        assertThat(response.getDocuments()).hasSize(1);
        var view = response.getDocuments().get(0);
        assertThat(view.getId()).isEqualTo("D1");
        assertThat(view.getVin()).isEqualTo(VIN);
        assertThat(view.getName()).isEqualTo("Invoice");
        assertThat(view.getType()).isEqualTo("PDF");
        assertThat(view.getUrl()).isEqualTo("https://ex.com/d1");
        assertThat(view.getCreatedAt()).isEqualTo("2024-01-01");
        assertThat(view.getSource()).isEqualTo("SALES");
    }

    @Test
    @DisplayName("Should return empty documents list and total=0 when no items")
    void shouldHandleEmptyDocumentList() {
        AggregatedDocuments aggregated = new AggregatedDocuments(VIN, Collections.emptyList());

        DocumentResponse response = sut.toResponse(aggregated);

        assertThat(response.getVin()).isEqualTo(VIN);
        assertThat(response.getTotal()).isEqualTo(0);
        assertThat(response.getDocuments()).isEmpty();
    }

    @Test
    @DisplayName("Should map multiple documents")
    void shouldMapMultipleDocuments() {
        List<DocumentItem> items = List.of(
            new DocumentItem("D1", VIN, "Invoice", "PDF", "https://ex.com/d1", "2024-01-01", "SALES"),
            new DocumentItem("D2", VIN, "Service Record", "PDF", "https://ex.com/d2", "2024-06-01", "SERVICE")
        );
        AggregatedDocuments aggregated = new AggregatedDocuments(VIN, items);

        DocumentResponse response = sut.toResponse(aggregated);

        assertThat(response.getTotal()).isEqualTo(2);
        assertThat(response.getDocuments()).hasSize(2);
    }
}
