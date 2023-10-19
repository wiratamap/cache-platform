package com.personal.cacheplatform;

import com.personal.cacheplatform.cache.CacheData;
import com.personal.cacheplatform.evictionstrategy.EvictionStrategyConstant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@ConfigurationPropertiesScan("com.personal.cacheplatform.properties")
public class CachePlatformApplication {

  public static void main(String[] args) {
    SpringApplication.run(CachePlatformApplication.class, args);
  }

  @Bean
  public Map<String, CacheData> cacheRepository() {
    return new HashMap<>();
  }

  @Bean
  public StringBuilder activeEvictionStrategy() {
    return new StringBuilder(EvictionStrategyConstant.TIME_BASED_EXPIRATION_STRATEGY);
  }

}
