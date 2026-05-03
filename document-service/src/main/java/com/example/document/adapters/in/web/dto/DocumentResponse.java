package com.example.document.adapters.in.web.dto;

import java.util.List;

public class DocumentResponse {

    private String vin;
    private int total;
    private List<DocumentView> documents;

    public DocumentResponse() {}

    public DocumentResponse(String vin, int total, List<DocumentView> documents) {
        this.vin = vin;
        this.total = total;
        this.documents = documents;
    }

    public String getVin() { return vin; }
    public int getTotal() { return total; }
    public List<DocumentView> getDocuments() { return documents; }
}
