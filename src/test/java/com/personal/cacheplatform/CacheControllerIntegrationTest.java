package com.personal.cacheplatform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.cacheplatform.cache.CacheData;
import com.personal.cacheplatform.cache.CacheDataRequestDto;
import com.personal.cacheplatform.cache.CacheDataResponseDto;
import com.personal.cacheplatform.properties.CachePlatformProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@AutoConfigureWebTestClient
class CacheControllerIntegrationTest {

  @Autowired
  private WebTestClient client;

  @Autowired
  private CachePlatformProperties cachePlatformProperties;

  @Autowired
  private Map<String, CacheData> cacheRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @AfterEach
  public void tearDown() {
    cacheRepository.clear();
  }

  @Test
  void put_whenThereAreaAvailableCapacity_shouldPutNewKeyCacheData() throws JsonProcessingException {
    CacheDataRequestDto requestDto = CacheDataRequestDto.builder()
      .key("new-key")
      .content("content")
      .build();
    String jsonBody = objectMapper.writeValueAsString(requestDto);

    client.post()
      .uri(uriBuilder -> uriBuilder.path("/caches")
        .build())
      .header("content-type", MediaType.APPLICATION_JSON_VALUE)
      .bodyValue(jsonBody)
      .exchange()
      .expectStatus()
      .isCreated()
      .expectBody(new ParameterizedTypeReference<CacheDataResponseDto>() {});

    CacheData actualCachedData = cacheRepository.get(requestDto.getKey());
    assertNotNull(actualCachedData);
  }

  @Test
  void put_whenThereAreaNoAvailableCapacity_shouldThrowBadRequest() throws JsonProcessingException {
    cachePlatformProperties.setMaximumCacheSize(0L);
    CacheDataRequestDto requestDto = CacheDataRequestDto.builder()
      .key("new-key")
      .content("content")
      .build();
    String jsonBody = objectMapper.writeValueAsString(requestDto);

    client.post()
      .uri(uriBuilder -> uriBuilder.path("/caches")
        .build())
      .header("content-type", MediaType.APPLICATION_JSON_VALUE)
      .bodyValue(jsonBody)
      .exchange()
      .expectStatus()
      .isBadRequest();
  }

  @Test
  void get_whenCacheDataWithGivenKeyIsExist_shouldReturnCachedData() {
    CacheData cacheData = CacheData.builder()
      .key("key")
      .content("content")
      .expiredAt(System.currentTimeMillis() + 60_000L)
      .build();
    cacheRepository.put(cacheData.getKey(), cacheData);

    CacheDataResponseDto response = client.get()
      .uri(uriBuilder -> uriBuilder.path("/caches/{key}")
        .build(cacheData.getKey()))
      .exchange()
      .expectStatus()
      .isOk()
      .expectBody(new ParameterizedTypeReference<CacheDataResponseDto>() {
      })
      .returnResult()
      .getResponseBody();

    assertEquals(cacheData.getKey(), response.getKey());
    assertEquals(cacheData.getContent(), response.getContent());
  }

  @Test
  void get_whenCacheDataWithGivenKeyIsNotExist_shouldReturnNullContent() {
    String key = "not-exist-cache-key";

    CacheDataResponseDto response = client.get()
      .uri(uriBuilder -> uriBuilder.path("/caches/{key}")
        .build(key))
      .exchange()
      .expectStatus()
      .isOk()
      .expectBody(new ParameterizedTypeReference<CacheDataResponseDto>() {
      })
      .returnResult()
      .getResponseBody();

    assertEquals(key, response.getKey());
    assertNull(response.getContent());
  }
}
