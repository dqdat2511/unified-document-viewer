package com.example.document.application.service.cache;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.document.application.port.out.CachePort;
import com.example.document.infrastructure.config.CacheProperties;
import com.example.document.domain.exception.DocumentErrorCode;
import com.example.document.domain.exception.DocumentRuntimeException;
import com.example.document.infrastructure.cache.CacheService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;


public class CommonCacheService<T> implements CachePort<T> {

    private final CacheService cacheService;
    private final String useCase;
    private final CacheProperties.CacheConfig cacheConfig;
    private final TypeReference<T> typeReference;

    public CommonCacheService(CacheService cacheService, String useCase,
                              CacheProperties.CacheConfig cacheConfig, TypeReference<T> typeReference) {
        this.cacheService = cacheService;
        this.useCase = useCase;
        this.cacheConfig = cacheConfig;
        this.typeReference = typeReference;
    }

  @Override
  public T getCachedEntity(String referenceNumber) {
    try {
      String key = cacheService.generateCacheKey(useCase, referenceNumber);
      return cacheService.getObject(key, typeReference);
    } catch (JsonProcessingException e) {
      throw new DocumentRuntimeException(DocumentErrorCode.INTERNAL, "Failed to get cached data", e);
    }
  }

  @Override
  public void cacheEntity(T entity, String referenceNumber) {
    try {
      String key = cacheService.generateCacheKey(useCase, referenceNumber);
      cacheService.setObject(key, entity, cacheConfig.getExpirationMs());
    } catch (JsonProcessingException e) {
      throw new DocumentRuntimeException(DocumentErrorCode.INTERNAL, "Failed to cache data", e);
    }
  }

  @Override
  public void cacheEntities(List<T> entities, String identifier) {
    try {
      String key = cacheService.generateCacheKey(useCase, identifier);
      cacheService.setObject(key, entities, cacheConfig.getExpirationMs());
    } catch (JsonProcessingException e) {
      throw new DocumentRuntimeException(DocumentErrorCode.INTERNAL, "Failed to cache data", e);
    }
  }

  @Override
  public void addMultipleObject(Map<String, T> data) {
    Map<String, T> entities =
        data.entrySet().stream()
            .collect(
                Collectors.toMap(
                    entry -> cacheService.generateCacheKey(useCase, entry.getKey()),
                    Map.Entry::getValue));

    cacheService.addMultipleObject(entities, cacheConfig.getExpirationMs());
  }

  @Override
  public void clearCachedEntity(String referenceNumber) {
    try {
      String key = cacheService.generateCacheKey(useCase, referenceNumber);
      cacheService.deleteStringData(key);
    } catch (Exception e) {
      throw new DocumentRuntimeException(
          DocumentErrorCode.INTERNAL, "Failed to clear cached data", e);
    }
  }

  @Override
  public void cacheEntityInSortedSet(T entity, String referenceNumber, long score) {
    try {
      String key = cacheService.generateCacheKey(useCase, referenceNumber);
      cacheService.addToSortedSet(key, entity, score, cacheConfig.getExpirationMs());
    } catch (JsonProcessingException e) {
      throw new DocumentRuntimeException(
          DocumentErrorCode.INTERNAL, "Failed to cache entity with score", e);
    }
  }

  @Override
  public void cacheMultipleEntitiesInSortedSet(
      Map<T, Long> entityScoreMap, String referenceNumber) {
    try {
      String key = cacheService.generateCacheKey(useCase, referenceNumber);
      cacheService.addMultipleToSortedSet(
          key, new java.util.HashMap<>(entityScoreMap), cacheConfig.getExpirationMs());
    } catch (JsonProcessingException e) {
      throw new DocumentRuntimeException(
          DocumentErrorCode.INTERNAL, "Failed to cache multiple entities in sorted set", e);
    }
  }

  @Override
  public List<T> getAllSortedSetCachedEntity(String referenceNumber) {
    try {
      String key = cacheService.generateCacheKey(useCase, referenceNumber);
      // Get all items from sorted set (0 to -1 means all items)
      Set<T> result = cacheService.getSortedSetRange(key, 0, -1, typeReference);
      return new ArrayList<>(result);
    } catch (JsonProcessingException e) {
      throw new DocumentRuntimeException(
          DocumentErrorCode.INTERNAL, "Failed to get all cached entities from sorted set", e);
    }
  }

  @Override
  public List<T> getSortedSetRange(String referenceNumber, long start, long end) {
    try {
      String key = cacheService.generateCacheKey(useCase, referenceNumber);
      Set<T> result = cacheService.getSortedSetRange(key, start, end, typeReference);
      return new ArrayList<>(result);
    } catch (JsonProcessingException e) {
      throw new DocumentRuntimeException(
          DocumentErrorCode.INTERNAL, "Failed to get sorted set range from cache", e);
    }
  }

  @Override
  public <C extends Collection<T>> C getCachedEntities(String identifier, TypeReference<C> typeReference) {
    try {
      String key = cacheService.generateCacheKey(useCase, identifier);
      return cacheService.getObject(key, typeReference);
    } catch (JsonProcessingException e) {
      throw new DocumentRuntimeException(DocumentErrorCode.INTERNAL, "Failed to get cached data", e);
    }
  }  
}
