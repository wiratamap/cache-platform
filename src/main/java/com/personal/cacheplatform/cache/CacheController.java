package com.personal.cacheplatform.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class CacheController {

  private final CacheService cacheService;

  @PostMapping("/caches")
  public Mono<ResponseEntity<CacheDataResponseDto>> put(@RequestBody CacheDataRequestDto request) {
    return cacheService.put(request)
      .flatMap(response -> Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(response)));
  }

  @GetMapping("/caches/{key}")
  public Mono<ResponseEntity<CacheDataResponseDto>> get(@PathVariable String key) {
    return cacheService.get(key)
      .flatMap(response -> Mono.just(ResponseEntity.status(HttpStatus.OK).body(response)));
  }
}
