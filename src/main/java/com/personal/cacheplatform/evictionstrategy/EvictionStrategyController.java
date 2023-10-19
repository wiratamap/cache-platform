package com.personal.cacheplatform.evictionstrategy;

import com.personal.cacheplatform.cache.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class EvictionStrategyController {

  private final CacheService cacheService;

  @PutMapping("/eviction-strategies")
  public Mono<ResponseEntity<EvictionStrategyDto>> changeEvictionStrategy(@RequestBody EvictionStrategyDto request) {
    return cacheService.changeEvictionStrategy(request)
      .flatMap(response -> Mono.just(ResponseEntity.status(HttpStatus.OK).body(response)));
  }

}
