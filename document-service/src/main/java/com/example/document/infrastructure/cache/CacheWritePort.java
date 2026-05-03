package com.example.document.infrastructure.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Map;

public interface CacheWritePort {

  void setStringData(String key, String value, Long expirationMs);

  Boolean setStringDataIfAbsent(String key, String value, Long expirationMs);

  void setObject(String key, Object value, Long expirationMs) throws JsonProcessingException;

  void updateStringData(String key, String value);

  void updateObject(String key, Object value) throws JsonProcessingException;

  void addToSortedSet(String key, Object value, long score, Long expirationMs)
      throws JsonProcessingException;

  void addMultipleToSortedSet(String key, Map<Object, Long> valueScoreMap, Long expirationMs)
      throws JsonProcessingException;

  void addMultipleObject(Map<String, ?> data, Long expirationMs);

  void addMultipleString(Map<String, String> data, Long expirationMs);
}
