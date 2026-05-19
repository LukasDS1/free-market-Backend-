package com.freemarket.config_service.service;

import org.springframework.stereotype.Service;

import com.freemarket.config_service.model.SystemConfig;
import com.freemarket.config_service.repository.SystemConfigRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemConfigService {

    private final SystemConfigRepository systemConfigRepo;

    public String getConfigValue(String key) {
        return systemConfigRepo.findByConfigKey(key)
            .orElseThrow(() -> new IllegalArgumentException("Config no encontrada: " + key))
            .getConfigValue();
    }

    public SystemConfig updateCountry(String countryCode, String countryName) {
        SystemConfig config = systemConfigRepo.findByConfigKey("search_country")
            .orElseThrow(() -> new IllegalArgumentException("Config no encontrada"));
        config.setConfigValue(countryCode.toLowerCase());
        return systemConfigRepo.save(config);
    }

    public SystemConfig getCountry() {
        return systemConfigRepo.findByConfigKey("search_country")
            .orElseThrow(() -> new IllegalArgumentException("Config no encontrada"));
    }
}