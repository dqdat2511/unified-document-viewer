package com.example.document.infrastructure.cache;

import java.util.List;

public interface CacheClearPort {

  Boolean deleteStringData(String key);

  void deleteByPrefix(String prefix);

  void deleteByMultiplePrefixes(List<String> prefixes);

  void deleteStringDataBatch(List<String> keys);
}
