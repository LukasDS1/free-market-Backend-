package com.freemarket.locations_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.freemarket.locations_service.response.SystemConfigResponse;

@FeignClient(name = "config-service", fallback = ConfigClientFallback.class)
public interface ConfigClient {

    @GetMapping("/api-v1/config/system/country")
    SystemConfigResponse getSearchCountry();
}