package com.personal.cacheplatform.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("cache-platform")
public class CachePlatformProperties {
  private Long maximumCacheSize = 3L;
  private Long defaultExpirationInMillis = 120_000L;
}
