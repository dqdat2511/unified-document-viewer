package com.example.document.infrastructure.config;

import com.example.document.application.port.out.CachePort;
import com.example.document.application.service.cache.CommonCacheServiceFactory;
import com.example.document.domain.DocumentItem;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring bean wiring specific to document-service application layer.
 * Provides named CachePort beans so services receive ready-to-use cache instances
 * without needing @PostConstruct factory calls.
 */
@Configuration
public class CacheServiceConfig {

    @Bean
    public CachePort<DocumentItem> documentItemCachePort(CommonCacheServiceFactory factory) {
        return factory.getCacheService("documentsByVin", new TypeReference<DocumentItem>() {});
    }
}
