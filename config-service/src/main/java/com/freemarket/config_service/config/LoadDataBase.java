package com.freemarket.config_service.config;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.freemarket.config_service.model.SystemConfig;
import com.freemarket.config_service.repository.SystemConfigRepository;
import com.freemarket.config_service.repository.ConfigRepository;

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

    @Bean
    CommandLineRunner initConfig(ConfigRepository configRepository) {
        return args -> {
            if (configRepository.count() == 0) {
                com.freemarket.config_service.model.Configuration config = new com.freemarket.config_service.model.Configuration();
                config.setIdUser(1L);
                config.setCommerceName("FreeMarket");
                config.setLogoUrl("");
                config.setFavicomUrl("");
                config.setPrimarColor("#2563EB");
                config.setSecondaryColor("#1D4ED8");
                config.setPrincipalfont("DM Sans");
                config.setUpdateAt(Date.valueOf(LocalDate.now()));
                configRepository.save(config);
                System.out.println("Configuración visual inicial creada.");
            }
        };
    }
}