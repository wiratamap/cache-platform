package com.personal.cacheplatform.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheData {

  private String key;

  private String content;

  private long expiredAt;

  private long lastAccessedAt;

  private long accessedCount;

}
