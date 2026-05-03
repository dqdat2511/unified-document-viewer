package com.example.document.infrastructure.cache;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@RequiredArgsConstructor
public abstract class BaseCacheService implements CacheService {
  @Value("${spring.application.env:local}")
  private String env;

  @Value("${spring.application.name:document-service}")
  private String appName;

  public String generateCacheKey(String useCase, String... components) {
    return String.format("%s:%s:%s:", env, appName, useCase) + String.join(":", components);
  }

  public List<String> buildSlotsByKeyParts(String... keyParts) {
    return Arrays.stream(keyParts).map(p -> "{" + p + "}").toList();
  }
}
