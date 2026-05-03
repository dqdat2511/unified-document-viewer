package com.example.document.infrastructure.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisService extends BaseCacheService {

  private static final long TTL_KEY_MISSING = -2L;
  private static final long TTL_PERSISTENT = -1L;

  private final StringRedisTemplate stringRedisTemplate;
  private final ObjectMapper objectMapper;

  @Override
  public void setStringData(String key, String value, Long expirationMs) {
    stringRedisTemplate.opsForValue().set(key, value, expirationMs, TimeUnit.MILLISECONDS);
  }

  @Override
  public String getStringData(String key) {
    return stringRedisTemplate.opsForValue().get(key);
  }

  @Override
  public Boolean setStringDataIfAbsent(String key, String value, Long expirationMs) {
    return stringRedisTemplate
        .opsForValue()
        .setIfAbsent(key, value, expirationMs, TimeUnit.MILLISECONDS);
  }

  @Override
  public Boolean deleteStringData(String key) {
    return stringRedisTemplate.delete(key);
  }

  @Override
  public void deleteByPrefix(String prefix) {
    ScanOptions options = ScanOptions.scanOptions().match(prefix + "*").count(100).build();

    try (Cursor<String> cursor = stringRedisTemplate.scan(options)) {
      List<String> keysToDelete = new ArrayList<>(64);
      cursor.forEachRemaining(keysToDelete::add);
      if (!keysToDelete.isEmpty()) {
        stringRedisTemplate.delete(keysToDelete);
      }
    }
  }

  @Override
  public void deleteByMultiplePrefixes(List<String> prefixes) {
    List<String> keysToDelete = new ArrayList<>(64);

    for (String prefix : prefixes) {
      ScanOptions options = ScanOptions.scanOptions().count(100).match(prefix + "*").build();
      try (Cursor<String> cursor = stringRedisTemplate.scan(options)) {
        cursor.forEachRemaining(keysToDelete::add);
      }
    }

    if (!keysToDelete.isEmpty()) {
      stringRedisTemplate.delete(keysToDelete);
    }
  }

  @Override
  public void deleteStringDataBatch(List<String> keys) {
    stringRedisTemplate.executePipelined(
        (RedisCallback<Object>)
            connection -> {
              for (String key : keys) {
                byte[] rawKey = stringRedisTemplate.getStringSerializer().serialize(key);
                connection.keyCommands().del(rawKey);
              }
              return null;
            });
  }

  @Override
  public void setObject(String key, Object value, Long expirationMs)
      throws JsonProcessingException {
    setStringData(key, objectMapper.writeValueAsString(value), expirationMs);
  }

  @Override
  public <T> T getObject(String key, TypeReference<T> typeReference)
      throws JsonProcessingException {
    String value = getStringData(key);
    if (value == null) {
      return null;
    }

    return objectMapper.readValue(value, typeReference);
  }

  @Override
  public Long getTTL(String key) {
    return stringRedisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
  }

  @Override
  public void updateStringData(String key, String value) {
    Long ttl = getTTL(key);

    // cannot get TTL, key does not exist
    if (ttl == null || ttl == TTL_KEY_MISSING) {
      return;
    }

    if (ttl == TTL_PERSISTENT) {
      stringRedisTemplate.opsForValue().set(key, value);
    } else {
      stringRedisTemplate.opsForValue().set(key, value, ttl, TimeUnit.MILLISECONDS);
    }
  }

  @Override
  public void updateObject(String key, Object value) throws JsonProcessingException {
    updateStringData(key, objectMapper.writeValueAsString(value));
  }

  @Override
  public void addToSortedSet(String key, Object value, long score, Long expirationMs)
      throws JsonProcessingException {
    String jsonValue = objectMapper.writeValueAsString(value);
    stringRedisTemplate.opsForZSet().add(key, jsonValue, score);
    if (expirationMs != null) {
      stringRedisTemplate.expire(key, expirationMs, TimeUnit.MILLISECONDS);
    }
  }

  @Override
  public void addMultipleToSortedSet(String key, Map<Object, Long> valueScoreMap, Long expirationMs)
      throws JsonProcessingException {
    if (valueScoreMap == null || valueScoreMap.isEmpty()) {
      return;
    }

    // Serialize all values to JSON
    Map<String, Double> serializedData = new HashMap<>();
    for (Map.Entry<Object, Long> entry : valueScoreMap.entrySet()) {
      String jsonValue = objectMapper.writeValueAsString(entry.getKey());
      serializedData.put(jsonValue, entry.getValue().doubleValue());
    }

    // Use pipeline for batch ZADD operations
    stringRedisTemplate.executePipelined(
        (RedisCallback<Object>)
            connection -> {
              byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

              // Add all values to sorted set in one pipeline
              for (Map.Entry<String, Double> entry : serializedData.entrySet()) {
                byte[] valueBytes = entry.getKey().getBytes(StandardCharsets.UTF_8);
                connection.zSetCommands().zAdd(keyBytes, entry.getValue(), valueBytes);
              }

              // Set expiration
              if (expirationMs != null) {
                connection.keyCommands().pExpire(keyBytes, expirationMs);
              }

              return null;
            });
  }

  @Override
  public <T> Set<T> getSortedSetRange(
      String key, long start, long end, TypeReference<T> typeReference)
      throws JsonProcessingException {
    Set<String> values = stringRedisTemplate.opsForZSet().range(key, start, end);
    if (values == null || values.isEmpty()) {
      return Collections.emptySet();
    }

    Set<T> result = new LinkedHashSet<>();
    for (String value : values) {
      result.add(objectMapper.readValue(value, typeReference));
    }
    return result;
  }

  @Override
  public void addMultipleObject(Map<String, ?> data, Long expirationMs) {
    if (data == null || data.isEmpty()) {
      return;
    }

    // Serialize all data to JSON
    Map<String, String> serializedValues = new HashMap<>();
    for (Map.Entry<String, ?> entry : data.entrySet()) {
      try {
        String jsonValue = objectMapper.writeValueAsString(entry.getValue());
        serializedValues.put(entry.getKey(), jsonValue);
      } catch (JsonProcessingException e) {
        log.warn("Failed to serialize value for key: {}", entry.getKey(), e);
      }
    }

    addMultipleString(serializedValues, expirationMs);
  }

  @Override
  public void addMultipleString(Map<String, String> data, Long expirationMs) {
    // Use pipeline for single round trip with all SET and EXPIRE commands
    stringRedisTemplate.executePipelined(
        (RedisCallback<Object>)
            connection -> {
              data.forEach(
                  (key, jsonValue) -> {
                    byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
                    byte[] valueBytes = jsonValue.getBytes(StandardCharsets.UTF_8);
                    connection.stringCommands().set(keyBytes, valueBytes);
                    if (expirationMs != null) {
                      connection.keyCommands().pExpire(keyBytes, expirationMs);
                    }
                  });
              return null;
            });
  }
}
