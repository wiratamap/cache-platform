package com.personal.cacheplatform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.cacheplatform.cache.CacheData;
import com.personal.cacheplatform.evictionstrategy.EvictionStrategyConstant;
import com.personal.cacheplatform.evictionstrategy.EvictionStrategyDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@AutoConfigureWebTestClient
class EvictionStrategyControllerIntegrationTest {

  @Autowired
  private WebTestClient client;

  @Autowired
  private Map<String, CacheData> cacheRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @AfterEach
  public void tearDown() {
    cacheRepository.clear();
  }

  @Test
  void changeEvictionStrategy_whenChangeToLeastFrequentlyUsedEvictionStrategy_shouldEvictLeastFrequentlyUsedCacheData() throws JsonProcessingException {
    CacheData firstCacheData = CacheData.builder()
      .key("key1")
      .content("content1")
      .accessedCount(400L)
      .build();
    CacheData secondCacheData = CacheData.builder()
      .key("key2")
      .content("content2")
      .accessedCount(500L)
      .build();
    cacheRepository.putAll(Map.of(firstCacheData.getKey(), firstCacheData,
      secondCacheData.getKey(), secondCacheData));
    EvictionStrategyDto evictionStrategyDto = EvictionStrategyDto.builder()
      .evictionStrategy(EvictionStrategyConstant.LEAST_FREQUENTLY_USED_STRATEGY)
      .build();
    String jsonBody = objectMapper.writeValueAsString(evictionStrategyDto);

    client.put()
      .uri(uriBuilder -> uriBuilder.path("/eviction-strategies")
        .build())
      .header("content-type", MediaType.APPLICATION_JSON_VALUE)
      .bodyValue(jsonBody)
      .exchange()
      .expectStatus()
      .isOk()
      .expectBody(new ParameterizedTypeReference<EvictionStrategyDto>() {});
    CacheData cacheData = cacheRepository.get(firstCacheData.getKey());
    CacheData unEvictedCachedData = cacheRepository.get(secondCacheData.getKey());

    assertNull(cacheData);
    assertNotNull(unEvictedCachedData);
  }

  @Test
  void changeEvictionStrategy_whenChangeToLeastRecentlyUsedEvictionStrategy_shouldEvictLeastRecentlyUsedCacheData() throws JsonProcessingException {
    long currentTime = System.currentTimeMillis();
    CacheData firstCacheData = CacheData.builder()
      .key("key1")
      .content("content1")
      .lastAccessedAt(currentTime - 60_000L)
      .build();
    CacheData secondCacheData = CacheData.builder()
      .key("key2")
      .content("content2")
      .lastAccessedAt(currentTime)
      .build();
    cacheRepository.putAll(Map.of(firstCacheData.getKey(), firstCacheData,
      secondCacheData.getKey(), secondCacheData));
    EvictionStrategyDto evictionStrategyDto = EvictionStrategyDto.builder()
      .evictionStrategy(EvictionStrategyConstant.LEAST_RECENTLY_USED_STRATEGY)
      .build();
    String jsonBody = objectMapper.writeValueAsString(evictionStrategyDto);

    client.put()
      .uri(uriBuilder -> uriBuilder.path("/eviction-strategies")
        .build())
      .header("content-type", MediaType.APPLICATION_JSON_VALUE)
      .bodyValue(jsonBody)
      .exchange()
      .expectStatus()
      .isOk()
      .expectBody(new ParameterizedTypeReference<EvictionStrategyDto>() {});
    CacheData cacheData = cacheRepository.get(firstCacheData.getKey());
    CacheData unEvictedCachedData = cacheRepository.get(secondCacheData.getKey());

    assertNull(cacheData);
    assertNotNull(unEvictedCachedData);
  }

  @Test
  void changeEvictionStrategy_whenChangeToTimeBasedExpirationEvictionStrategy_shouldEvictAllCacheDataAlreadyExpired() throws JsonProcessingException {
    long currentTime = System.currentTimeMillis();
    CacheData firstCacheData = CacheData.builder()
      .key("key1")
      .content("content1")
      .expiredAt(currentTime - 60_000L)
      .build();
    CacheData secondCacheData = CacheData.builder()
      .key("key2")
      .content("content2")
      .expiredAt(currentTime + 60_000L)
      .build();
    cacheRepository.putAll(Map.of(firstCacheData.getKey(), firstCacheData,
      secondCacheData.getKey(), secondCacheData));
    EvictionStrategyDto evictionStrategyDto = EvictionStrategyDto.builder()
      .evictionStrategy(EvictionStrategyConstant.TIME_BASED_EXPIRATION_STRATEGY)
      .build();
    String jsonBody = objectMapper.writeValueAsString(evictionStrategyDto);

    client.put()
      .uri(uriBuilder -> uriBuilder.path("/eviction-strategies")
        .build())
      .header("content-type", MediaType.APPLICATION_JSON_VALUE)
      .bodyValue(jsonBody)
      .exchange()
      .expectStatus()
      .isOk()
      .expectBody(new ParameterizedTypeReference<EvictionStrategyDto>() {});
    CacheData cacheData = cacheRepository.get(firstCacheData.getKey());
    CacheData unEvictedCachedData = cacheRepository.get(secondCacheData.getKey());

    assertNull(cacheData);
    assertNotNull(unEvictedCachedData);
  }
}
