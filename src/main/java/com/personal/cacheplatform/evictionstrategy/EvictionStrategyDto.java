package com.personal.cacheplatform.evictionstrategy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvictionStrategyDto {

  private String evictionStrategy;
}
