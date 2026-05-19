package com.freemarket.locations_service.client;

import org.springframework.stereotype.Component;

import com.freemarket.locations_service.response.SystemConfigResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ConfigClientFallback implements ConfigClient {

    @Override
    public SystemConfigResponse getSearchCountry() {
        log.warn("config-service no disponible, usando país por defecto: cl");
    return new SystemConfigResponse(null, "search_country", "cl", "Chile", "fallback");
    }
}