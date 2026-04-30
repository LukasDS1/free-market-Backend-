package com.freemarket.auth_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "state-service",fallback = FeignClientFallback.class)
public interface FeingClient {
    @GetMapping("/api-v1/state/{id}")
    String getStateById(@PathVariable("id") Long id);


}
