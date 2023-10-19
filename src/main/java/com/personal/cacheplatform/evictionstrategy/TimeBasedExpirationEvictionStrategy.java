package com.personal.cacheplatform.evictionstrategy;

import com.personal.cacheplatform.cache.CacheData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Function;

@Service(EvictionStrategyConstant.TIME_BASED_EXPIRATION_STRATEGY)
@RequiredArgsConstructor
public class TimeBasedExpirationEvictionStrategy implements ChangeAbleEvictionStrategy {

  private final Map<String, CacheData> cacheRepository;

  @Override
  public Mono<Boolean> evict() {
    long currentTime = System.currentTimeMillis();
    return Mono.fromSupplier(() -> cacheRepository)
      .map(Map::values)
      .flatMapMany(Flux::fromIterable)
      .filter(cacheData -> cacheData.getExpiredAt() > currentTime)
      .collectMap(CacheData::getKey, Function.identity())
      .flatMap(updatedCaches -> {
        cacheRepository.clear();
        cacheRepository.putAll(updatedCaches);
        return Mono.just(Boolean.TRUE);
      })
      .defaultIfEmpty(Boolean.FALSE)
      .onErrorReturn(Boolean.FALSE);
  }
}
