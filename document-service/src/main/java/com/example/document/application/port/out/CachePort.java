package com.example.document.application.port.out;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Outbound Port (Hexagonal Architecture)
 * Interface for cache storage service that application depends on.
 * Implemented by infrastructure adapters (RedisService, etc.).
 * Application calls this to cache/retrieve data.
 */
public interface CachePort<T> {
  T getCachedEntity(String identifier);

  void cacheEntity(T entity, String identifier);

  void cacheEntities(List<T> entities, String identifier);

  void addMultipleObject(Map<String, T> data);

  void clearCachedEntity(String identifier);

  void cacheEntityInSortedSet(T entity, String identifier, long score);

  void cacheMultipleEntitiesInSortedSet(Map<T, Long> entityScoreMap, String identifier);

  List<T> getAllSortedSetCachedEntity(String identifier);

  List<T> getSortedSetRange(String identifier, long start, long end);

  <C extends Collection<T>> C getCachedEntities(String identifier, TypeReference<C> typeReference);
}
