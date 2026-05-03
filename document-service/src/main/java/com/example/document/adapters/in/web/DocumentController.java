package com.example.document.adapters.in.web;

import com.example.document.adapters.in.web.converter.DocumentResponseConverter;
import com.example.document.application.port.in.AggregateDocumentsUseCase;
import com.example.document.adapters.in.web.dto.DocumentResponse;
import com.example.document.domain.AggregatedDocuments;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Inbound Adapter (Hexagonal Architecture) — REST Controller.
 * Accepts client requests, delegates to use case via inbound port.
 *
 * FIX (VIN Validation): Previous regex ^[A-Z0-9-]{3,32}$ was too loose.
 * ISO 3779 standard: exactly 17 chars, alphanumeric, excluding I, O, Q.
 */
@Validated
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final AggregateDocumentsUseCase aggregateDocumentsUseCase;
    private final DocumentResponseConverter documentResponseConverter;

    @GetMapping("/{vin}")
    public ResponseEntity<DocumentResponse> getDocumentsByVin(
        @PathVariable("vin")
        @Pattern(
            regexp = "^[A-HJ-NPR-Z0-9]{17}$",
            message = "{error.vin.invalid}"
        )
        String vin
    ) {
        AggregatedDocuments result = aggregateDocumentsUseCase.aggregateByVin(vin);
        DocumentResponse response = documentResponseConverter.toResponse(result);
        return ResponseEntity.ok(response);
    }
}
