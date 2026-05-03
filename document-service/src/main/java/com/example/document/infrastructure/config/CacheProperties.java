package com.example.document.infrastructure.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "caches")
public class CacheProperties {
  private Map<String, CacheConfig> useCases = new HashMap<>();

  @Getter
  @Setter
  public static class CacheConfig {
    private Long expirationMs;
  }

  public CacheConfig getCacheConfig(String key) {
    CacheConfig config = useCases.get(key);
    if (config == null) {
      throw new IllegalArgumentException("Cache config not found for key: " + key);
    }
    return config;
  }
}
