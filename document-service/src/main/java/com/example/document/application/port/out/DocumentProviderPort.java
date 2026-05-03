package com.example.document.application.port.out;

import com.example.document.domain.DocumentItem;
import java.util.List;


public interface DocumentProviderPort {

    /**
     * Fetch documents from one external system for the given VIN.
     *
     * @param vin the vehicle identification number
     * @return list of documents; empty list on failure (never null)
     */
    List<DocumentItem> fetchDocuments(String vin);


    String providerName();
}
