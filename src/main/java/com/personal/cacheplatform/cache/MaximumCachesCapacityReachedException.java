package com.personal.cacheplatform.cache;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Maximum caches capacity already reached")
public class MaximumCachesCapacityReachedException extends RuntimeException {
}
