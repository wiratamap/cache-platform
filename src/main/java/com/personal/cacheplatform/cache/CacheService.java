package com.personal.cacheplatform.cache;

import com.personal.cacheplatform.evictionstrategy.ChangeAbleEvictionStrategy;
import com.personal.cacheplatform.evictionstrategy.EvictionStrategyConstant;
import com.personal.cacheplatform.evictionstrategy.EvictionStrategyDto;
import com.personal.cacheplatform.properties.CachePlatformProperties;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CacheService {

  private final CachePlatformProperties cachePlatformProperties;

  private final Map<String, ChangeAbleEvictionStrategy> evictionStrategies;

  private final StringBuilder activeEvictionStrategy;

  private final Map<String, CacheData> cacheRepository;

  public Mono<EvictionStrategyDto> changeEvictionStrategy(EvictionStrategyDto request) {
    return Mono.fromSupplier(() -> request)
      .filter(Objects::nonNull)
      .map(EvictionStrategyDto::getEvictionStrategy)
      .filter(Objects::nonNull)
      .flatMap(newEvictionStrategy -> Mono.fromSupplier(() ->
          activeEvictionStrategy.replace(0,
            activeEvictionStrategy.length(),
            request.getEvictionStrategy())))
      .flatMap(activeStrategy -> evictionStrategies
        .get(activeStrategy.toString())
        .evict())
      .map(afterEvict -> request)
      .defaultIfEmpty(EvictionStrategyDto.builder()
        .evictionStrategy(activeEvictionStrategy.toString())
        .build())
      .onErrorReturn(EvictionStrategyDto.builder()
        .evictionStrategy(activeEvictionStrategy.toString())
        .build())
      .subscribeOn(Schedulers.boundedElastic());
  }

  public Mono<CacheDataResponseDto> put(CacheDataRequestDto request) {
    long currentTime = System.currentTimeMillis();

    return evictionStrategies.get(activeEvictionStrategy.toString())
      .evict()
      .filter(afterEvict ->
        cachePlatformProperties.getMaximumCacheSize() > cacheRepository.size())
      .flatMap(afterEvict ->
        Mono.just(Optional.ofNullable(cacheRepository.put(
          request.getKey(),
            toCacheData(request, currentTime)))
          .orElseGet(() -> toCacheData(request, currentTime))))
      .map(savedCachedData -> toCachedDataResponseDto(request.getKey(), savedCachedData))
      .switchIfEmpty(Mono.defer(() -> Mono.error(new MaximumCachesCapacityReachedException())))
      .subscribeOn(Schedulers.boundedElastic());
  }

  public Mono<CacheDataResponseDto> get(String key) {
    long currentTime = System.currentTimeMillis();

    return Mono.fromSupplier(() -> activeEvictionStrategy.toString().equals(EvictionStrategyConstant.TIME_BASED_EXPIRATION_STRATEGY))
      .filter(eligibleToEvict -> eligibleToEvict)
      .flatMap(evictAction -> evictionStrategies.get(activeEvictionStrategy.toString())
        .evict()
        .flatMap(afterEvict -> updateCacheData(key, currentTime))
      )
      .switchIfEmpty(Mono.defer(() -> updateCacheData(key, currentTime)))
      .subscribeOn(Schedulers.boundedElastic());
  }

  private Mono<CacheDataResponseDto> updateCacheData(String key, long currentTime) {
    CacheData cacheData = cacheRepository.getOrDefault(key, toDefaultEmptyCacheData());
    if (StringUtils.isNotEmpty(cacheData.getKey())) {
      cacheData.setAccessedCount(cacheData.getAccessedCount() + 1);
      cacheData.setLastAccessedAt(currentTime);
      cacheRepository.put(key, cacheData);
    }
    return Mono.just(toCachedDataResponseDto(key, cacheData));
  }

  private CacheData toDefaultEmptyCacheData() {
    return CacheData.builder()
      .build();
  }

  private CacheData toCacheData(CacheDataRequestDto request, long currentTime) {
    return CacheData.builder()
      .key(request.getKey())
      .content(request.getContent())
      .accessedCount(0)
      .lastAccessedAt(currentTime)
      .expiredAt(Optional.ofNullable(request)
        .map(CacheDataRequestDto::getExpiredAt)
        .orElse(currentTime + cachePlatformProperties.getDefaultExpirationInMillis()))
      .build();
  }

  private CacheDataResponseDto toCachedDataResponseDto(String key, CacheData cacheData) {
    return CacheDataResponseDto.builder()
      .key(key)
      .content(cacheData.getContent())
      .accessedCount(cacheData.getAccessedCount())
      .lastAccessedAt(cacheData.getLastAccessedAt())
      .expiredAt(cacheData.getExpiredAt())
      .build();
  }
}
