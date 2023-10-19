package com.personal.cacheplatform.evictionstrategy;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EvictionStrategyConstant {

  public static final String TIME_BASED_EXPIRATION_STRATEGY = "TIME_BASED_EXPIRATION_STRATEGY";
  public static final String LEAST_FREQUENTLY_USED_STRATEGY = "LEAST_FREQUENTLY_USED_STRATEGY";
  public static final String LEAST_RECENTLY_USED_STRATEGY = "LEAST_RECENTLY_USED_STRATEGY";

  public static final String DEFAULT_KEY = "DEFAULT_KEY";

}
