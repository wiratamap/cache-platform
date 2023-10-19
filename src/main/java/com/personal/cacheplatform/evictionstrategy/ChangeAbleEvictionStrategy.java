package com.personal.cacheplatform.evictionstrategy;

import reactor.core.publisher.Mono;

public interface ChangeAbleEvictionStrategy {
  Mono<Boolean> evict();
}
