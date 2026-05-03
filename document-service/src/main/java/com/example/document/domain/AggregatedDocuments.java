package com.example.document.domain;

import java.util.List;

public class AggregatedDocuments {

    private final String vin;
    private final List<DocumentItem> documents;

    public AggregatedDocuments(String vin, List<DocumentItem> documents) {
        this.vin = vin;
        this.documents = documents;
    }

    public String getVin() {
        return vin;
    }

    public List<DocumentItem> getDocuments() {
        return documents;
    }
}
