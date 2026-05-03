package com.example.document.application.service.cache;


import com.example.document.application.port.out.CachePort;
import com.example.document.infrastructure.config.CacheProperties;
import com.example.document.infrastructure.cache.CacheService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommonCacheServiceFactory {
  private final CacheService cacheService;
  private final CacheProperties cacheProperties;

  public <T> CachePort<T> getCacheService(String useCase, TypeReference<T> typeReference) {
    CacheProperties.CacheConfig config = cacheProperties.getCacheConfig(useCase);
    return new CommonCacheService<>(cacheService, useCase, config, typeReference);
  }
}
