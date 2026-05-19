package com.freemarket.locations_service.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.freemarket.locations_service.client.ConfigClient;
import com.freemarket.locations_service.response.SystemConfigResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CountryConfigService {

    private final ConfigClient configClient;

    @Cacheable("countryConfig")
    public SystemConfigResponse getCountryConfig() {
        log.info("Consultando config-service...");
        return configClient.getSearchCountry();
    }
}