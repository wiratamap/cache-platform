package com.personal.cacheplatform.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheDataResponseDto {
  private String key;

  private String content;

  private Long expiredAt;

  private Long lastAccessedAt;

  private Long accessedCount;
}
