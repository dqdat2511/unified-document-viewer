package com.example.document.adapter;

import com.example.document.adapters.in.web.DocumentController;
import com.example.document.adapters.in.web.converter.DocumentResponseConverter;
import com.example.document.application.port.in.AggregateDocumentsUseCase;
import com.example.document.domain.AggregatedDocuments;
import com.example.document.domain.DocumentItem;
import com.example.document.domain.exception.DocumentErrorCode;
import com.example.document.domain.exception.DocumentRuntimeException;
import com.example.document.domain.exception.ErrorCodeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentController.class)
@WithMockUser
@DisplayName("DocumentController — Web Layer Tests")
class DocumentControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean AggregateDocumentsUseCase aggregateDocumentsUseCase;
    @MockBean DocumentResponseConverter documentResponseConverter;
    @MockBean ErrorCodeProvider errorCodeProvider;

    private static final String VALID_VIN = "1HGBH41JXMN109186";

    @BeforeEach
    void setUpErrorCodeProvider() {
        when(errorCodeProvider.getValidationError()).thenReturn(DocumentErrorCode.VALIDATION);
        when(errorCodeProvider.getConstraintError()).thenReturn(DocumentErrorCode.CONSTRAINT);
        when(errorCodeProvider.getInternalError()).thenReturn(DocumentErrorCode.INTERNAL);
    }

    @Test
    @DisplayName("GET /api/v1/documents/{vin} returns 200 with valid VIN")
    void shouldReturn200ForValidVin() throws Exception {
        AggregatedDocuments agg = new AggregatedDocuments(VALID_VIN,
            List.of(new DocumentItem("D1", VALID_VIN, "Invoice", "PDF", "https://ex.com", "2024-01-01", "SALES")));
        when(aggregateDocumentsUseCase.aggregateByVin(VALID_VIN)).thenReturn(agg);
        // converter returns actual response - skip detailed response body test here

        mockMvc.perform(get("/api/v1/documents/{vin}", VALID_VIN))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/documents/{vin} returns 400 for VIN shorter than 17 chars")
    void shouldReturn400ForShortVin() throws Exception {
        mockMvc.perform(get("/api/v1/documents/SHORT123"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/documents/{vin} returns 400 for VIN with invalid chars (I, O, Q)")
    void shouldReturn400ForVinWithForbiddenChars() throws Exception {
        // VIN with 'I' — forbidden in ISO 3779
        mockMvc.perform(get("/api/v1/documents/1HGBH41JXMN10918I"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/documents/{vin} returns 404 when VIN not active")
    void shouldReturn404WhenVinNotActive() throws Exception {
        when(aggregateDocumentsUseCase.aggregateByVin(anyString()))
            .thenThrow(new DocumentRuntimeException(DocumentErrorCode.DOCUMENT_NOT_FOUND, "Not found"));

        mockMvc.perform(get("/api/v1/documents/{vin}", VALID_VIN))
            .andExpect(status().isNotFound());
    }
}
