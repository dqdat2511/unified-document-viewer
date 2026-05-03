package com.example.document.application.service;

import com.example.document.application.port.out.DocumentProviderPort;
import com.example.document.domain.DocumentItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;


@Service
public class DocumentFetchService {

    private static final Logger log = LoggerFactory.getLogger(DocumentFetchService.class);

    private final List<DocumentProviderPort> providers;
    private final Executor documentFetchExecutor;

    @Value("${document.fetch.timeout-seconds:5}")
    private int fetchTimeoutSeconds;

    public DocumentFetchService(List<DocumentProviderPort> providers,
                                Executor documentFetchExecutor) {
        this.providers = List.copyOf(providers);
        this.documentFetchExecutor = documentFetchExecutor;
    }

    /**
     * Fetches documents from ALL registered providers in parallel.
     * Each provider failure is isolated — partial results are returned.
     */
    public List<DocumentItem> fetchAll(String vin) {
        log.info("Fetching documents for vin={} from {} provider(s)", vin, providers.size());

        List<CompletableFuture<List<DocumentItem>>> futures = providers.stream()
            .map(provider -> CompletableFuture
                .supplyAsync(() -> provider.fetchDocuments(vin), documentFetchExecutor)
                .orTimeout(fetchTimeoutSeconds, TimeUnit.SECONDS)
                .handle((result, ex) -> {
                    if (ex != null) {
                        log.error("Provider [{}] timed out or failed for vin={}: {}",
                            provider.providerName(), vin, ex.getMessage());
                        return List.<DocumentItem>of();
                    }
                    log.info("Provider [{}] returned {} doc(s) for vin={}",
                        provider.providerName(), result.size(), vin);
                    return result;
                })
            ).toList();

        List<DocumentItem> combined = new ArrayList<>();
        futures.forEach(f -> combined.addAll(f.join()));

        log.info("Combined {} document(s) for vin={}", combined.size(), vin);
        return combined;
    }
}
