package com.freemarket.config_service.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.freemarket.config_service.model.SystemConfig;
import com.freemarket.config_service.repository.SystemConfigRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor

public class LoadDataBase {
@Bean
CommandLineRunner initSystemConfig(SystemConfigRepository systemConfigRepo) {
    return args -> {
        if (!systemConfigRepo.existsByConfigKey("search_country")) { 
            SystemConfig config = new SystemConfig();
            config.setConfigKey("search_country");
            config.setConfigValue("cl");
            config.setCountryName("Chile"); 
            config.setDescription("Código de país para búsqueda de ubicaciones en Nominatim");
            systemConfigRepo.save(config);
        }
    };
}

}
