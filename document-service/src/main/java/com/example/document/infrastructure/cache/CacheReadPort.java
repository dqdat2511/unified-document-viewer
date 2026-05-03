package com.example.document.infrastructure.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Set;

public interface CacheReadPort {

  String getStringData(String key);

  <T> T getObject(String key, TypeReference<T> typeReference) throws JsonProcessingException;

  Long getTTL(String key);

  <T> Set<T> getSortedSetRange(String key, long start, long end, TypeReference<T> typeReference)
      throws JsonProcessingException;

  String generateCacheKey(String useCase, String... components);

  List<String> buildSlotsByKeyParts(String... keyParts);
}
