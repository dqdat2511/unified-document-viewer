package com.example.document.adapters.in.web.converter;

import com.example.document.adapters.in.web.dto.DocumentResponse;
import com.example.document.adapters.in.web.dto.DocumentView;
import com.example.document.domain.AggregatedDocuments;
import com.example.document.domain.DocumentItem;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentResponseConverter {
    private final ModelMapper modelMapper;

    public DocumentResponse toResponse(AggregatedDocuments result) {
        List<DocumentView> documents = result.getDocuments().stream()
            .map(this::toDocumentView)
            .toList();
        return new DocumentResponse(result.getVin(), documents.size(), documents);
    }

    private DocumentView toDocumentView(DocumentItem documentItem) {
        return modelMapper.map(documentItem, DocumentView.class);
    }
}