package com.freemarket.config_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.freemarket.config_service.client.AuthClient;
import com.freemarket.config_service.exception.ServiceUnavailableException;
import com.freemarket.config_service.model.Configuration;
import com.freemarket.config_service.repository.ConfigRepository;
import com.freemarket.config_service.request.ConfigRequest;
import com.freemarket.config_service.response.ConfigResponse;

@ExtendWith(MockitoExtension.class)
public class ConfigServiceTest {

    @Mock
    private ConfigRepository configRepo;
 
    @Mock
    private AuthClient rest;
 
    @InjectMocks
    private configService configService;
 
 
    private ConfigRequest buildRequest() {
        ConfigRequest req = new ConfigRequest();
        req.setIdUser(1L);
        req.setCommerceName("MiTienda");
        req.setLogoUrl("http://logo.com");
        req.setFavicomUrl("http://favicon.com");
        req.setPrimaryColor("#FFFFFF");
        req.setSecondaryColor("#000000");
        req.setPrincipalFont("Arial");
        return req;
    }
 
    private Configuration buildConfig() {
        Configuration config = new Configuration();
        config.setIdUser(1L);
        config.setCommerceName("MiTienda");
        config.setLogoUrl("http://logo.com");
        config.setFavicomUrl("http://favicon.com");
        config.setPrimarColor("#FFFFFF");
        config.setSecondaryColor("#000000");
        config.setPrincipalfont("Arial");
        config.setUpdateAt(Date.valueOf(LocalDate.now()));
        return config;
    }
 
    
 
    @Test
    void createConfiguration_success_returnsConfigResponse() {
        ConfigRequest request = buildRequest();
        Configuration saved = buildConfig();
 
        when(rest.getUserById(1L)).thenReturn(true);
        when(configRepo.existsByIdUser(1L)).thenReturn(false);
        when(configRepo.save(any(Configuration.class))).thenReturn(saved);
 
        ConfigResponse response = configService.createConfiguration(request);
 
        assertThat(response.getCommerceName()).isEqualTo("MiTienda");
        assertThat(response.getPrimaryColor()).isEqualTo("#FFFFFF");
    }
 
    @Test
    void createConfiguration_userNotFound_throwsIllegalArgument() {
        ConfigRequest request = buildRequest();
        when(rest.getUserById(1L)).thenReturn(false);
 
        assertThatThrownBy(() -> configService.createConfiguration(request))
                .isInstanceOf(IllegalArgumentException.class);
    }
 
    @Test
    void createConfiguration_serviceUnavailable_throwsServiceUnavailable() {
        ConfigRequest request = buildRequest();
        when(rest.getUserById(1L)).thenReturn(null);
 
        assertThatThrownBy(() -> configService.createConfiguration(request))
                .isInstanceOf(ServiceUnavailableException.class);
    }
 
    @Test
    void createConfiguration_configAlreadyExists_throwsIllegalState() {
        ConfigRequest request = buildRequest();
        when(rest.getUserById(1L)).thenReturn(true);
        when(configRepo.existsByIdUser(1L)).thenReturn(true);
 
        assertThatThrownBy(() -> configService.createConfiguration(request))
                .isInstanceOf(IllegalStateException.class);
    }
 

 
    @Test
    void updateConfiguration_success_returnsUpdatedConfigResponse() {
        ConfigRequest request = buildRequest();
        request.setCommerceName("NuevoNombre");
        Configuration existing = buildConfig();
        Configuration saved = buildConfig();
        saved.setCommerceName("NuevoNombre");
 
        when(rest.getUserById(1L)).thenReturn(true);
        when(configRepo.findByIdUser(1L)).thenReturn(Optional.of(existing));
        when(configRepo.save(any(Configuration.class))).thenReturn(saved);
 
        ConfigResponse response = configService.updateConfiguration(1L, request);
 
        assertThat(response.getCommerceName()).isEqualTo("NuevoNombre");
    }
 
    @Test
    void updateConfiguration_configNotFound_throwsIllegalState() {
        ConfigRequest request = buildRequest();
        when(rest.getUserById(1L)).thenReturn(true);
        when(configRepo.findByIdUser(1L)).thenReturn(Optional.empty());
 
        assertThatThrownBy(() -> configService.updateConfiguration(1L, request))
                .isInstanceOf(IllegalStateException.class);
    }
 
   
 
    @Test
    void getConfigurationByIdUser_success_returnsConfigResponse() {
        Configuration config = buildConfig();
        when(configRepo.findByIdUser(1L)).thenReturn(Optional.of(config));
 
        ConfigResponse response = configService.getConfigurationByIdUser(1L);
 
        assertThat(response.getCommerceName()).isEqualTo("MiTienda");
    }
 
    @Test
    void getConfigurationByIdUser_notFound_throwsIllegalState() {
        when(configRepo.findByIdUser(99L)).thenReturn(Optional.empty());
 
        assertThatThrownBy(() -> configService.getConfigurationByIdUser(99L))
                .isInstanceOf(IllegalStateException.class);
    }

}
