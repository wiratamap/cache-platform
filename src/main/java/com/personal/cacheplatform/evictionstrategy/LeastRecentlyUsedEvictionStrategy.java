package com.personal.cacheplatform.evictionstrategy;

import com.personal.cacheplatform.cache.CacheData;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

@Service(EvictionStrategyConstant.LEAST_RECENTLY_USED_STRATEGY)
@RequiredArgsConstructor
public class LeastRecentlyUsedEvictionStrategy implements ChangeAbleEvictionStrategy {

  private final Map<String, CacheData> cacheRepository;

  @Override
  public Mono<Boolean> evict() {
    return Mono.fromSupplier(() -> cacheRepository)
      .map(Map::values)
      .flatMapMany(Flux::fromIterable)
      .sort(Comparator.comparingLong(CacheData::getLastAccessedAt))
      .elementAt(0)
      .filter(Objects::nonNull)
      .filter(cacheData -> StringUtils.isNotEmpty(cacheData.getKey()))
      .flatMap(cacheData -> {
        cacheRepository.remove(cacheData.getKey());
        return  Mono.just(Boolean.TRUE);
      })
      .defaultIfEmpty(Boolean.FALSE)
      .onErrorReturn(Boolean.FALSE);
  }
}
