package com.freemarket.config_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.freemarket.config_service.client.AuthClient;
import com.freemarket.config_service.exception.ServiceUnavailableException;
import com.freemarket.config_service.messaging.ConfigPendienteProducer;
import com.freemarket.config_service.messaging.event.ConfigPendienteEvent;
import com.freemarket.config_service.model.Configuration;
import com.freemarket.config_service.repository.ConfigRepository;
import com.freemarket.config_service.request.ConfigRequest;
import com.freemarket.config_service.response.ConfigResponse;

@ExtendWith(MockitoExtension.class)
public class ConfigServiceTest {

    @Mock private ConfigRepository configRepo;
    @Mock private AuthClient rest;
    @Mock private ConfigPendienteProducer pendienteProducer;

    @InjectMocks
    private configService configService;

    // ─── builders 

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
        config.setIdConfig(1L);
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

    // ─── createConfiguration 

    @Test
    void createConfiguration_success_returnsConfigResponse() {
        Configuration saved = buildConfig();

        when(rest.getUserById(1L)).thenReturn(true);
        when(configRepo.existsByIdUser(1L)).thenReturn(false);
        when(configRepo.save(any(Configuration.class))).thenReturn(saved);

        ConfigResponse response = configService.createConfiguration(buildRequest());

        assertThat(response.getCommerceName()).isEqualTo("MiTienda");
        assertThat(response.getPrimaryColor()).isEqualTo("#FFFFFF");
    }

    @Test
    void createConfiguration_userNotFound_throwsIllegalArgument() {
        when(rest.getUserById(1L)).thenReturn(false);

        assertThatThrownBy(() -> configService.createConfiguration(buildRequest()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createConfiguration_serviceUnavailable_throwsServiceUnavailable() {
        // exist == null → encolarCreate() se ejecuta antes de lanzar la excepción
        when(rest.getUserById(1L)).thenReturn(null);
        doNothing().when(pendienteProducer)
                   .enviarConfigPendiente(any(ConfigPendienteEvent.class));

        assertThatThrownBy(() -> configService.createConfiguration(buildRequest()))
                .isInstanceOf(ServiceUnavailableException.class);
    }

    @Test
    void createConfiguration_configAlreadyExists_throwsIllegalState() {
        when(rest.getUserById(1L)).thenReturn(true);
        when(configRepo.existsByIdUser(1L)).thenReturn(true);

        assertThatThrownBy(() -> configService.createConfiguration(buildRequest()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void createConfiguration_emptyCommerceName_throwsIllegalArgument() {
        ConfigRequest request = buildRequest();
        request.setCommerceName("");

        when(rest.getUserById(1L)).thenReturn(true);
        when(configRepo.existsByIdUser(1L)).thenReturn(false);

        assertThatThrownBy(() -> configService.createConfiguration(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createConfiguration_emptyLogoUrl_throwsIllegalArgument() {
        ConfigRequest request = buildRequest();
        request.setLogoUrl("");

        when(rest.getUserById(1L)).thenReturn(true);
        when(configRepo.existsByIdUser(1L)).thenReturn(false);

        assertThatThrownBy(() -> configService.createConfiguration(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ─── updateConfiguration 

    @Test
    void updateConfiguration_success_returnsUpdatedConfigResponse() {
        Configuration existing = buildConfig();
        Configuration saved = buildConfig();
        saved.setCommerceName("NuevoNombre");

        // idConfig = 1L (el mismo que el id del buildConfig)
        when(configRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(configRepo.save(any(Configuration.class))).thenReturn(saved);

        ConfigRequest request = buildRequest();
        request.setCommerceName("NuevoNombre");

        ConfigResponse response = configService.updateConfiguration(1L, request);

        assertThat(response.getCommerceName()).isEqualTo("NuevoNombre");
    }

    @Test
    void updateConfiguration_configNotFound_throwsIllegalState() {
        when(configRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> configService.updateConfiguration(99L, buildRequest()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void updateConfiguration_emptyCommerceName_throwsIllegalArgument() {
        Configuration existing = buildConfig();
        when(configRepo.findById(1L)).thenReturn(Optional.of(existing));

        ConfigRequest request = buildRequest();
        request.setCommerceName("");

        assertThatThrownBy(() -> configService.updateConfiguration(1L, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateConfiguration_nullFieldsAreIgnored_returnsOk() {
        Configuration existing = buildConfig();
        Configuration saved = buildConfig();

        when(configRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(configRepo.save(any(Configuration.class))).thenReturn(saved);

        ConfigRequest request = new ConfigRequest();
        request.setIdUser(1L);
        request.setCommerceName("Solo esto cambia");

        ConfigResponse response = configService.updateConfiguration(1L, request);

        assertThat(response).isNotNull();
    }

    // ─── getConfigurationByIdUser 

    @Test
    void getConfigurationByIdUser_success_returnsConfigResponse() {
        when(configRepo.findByIdUser(1L)).thenReturn(Optional.of(buildConfig()));

        ConfigResponse response = configService.getConfigurationByIdUser(1L);

        assertThat(response.getCommerceName()).isEqualTo("MiTienda");
        assertThat(response.getPrimaryColor()).isEqualTo("#FFFFFF");
    }

    @Test
    void getConfigurationByIdUser_notFound_throwsIllegalState() {
        when(configRepo.findByIdUser(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> configService.getConfigurationByIdUser(99L))
                .isInstanceOf(IllegalStateException.class);
    }

    // ─── getPublicConfiguration 
    @Test
    void getPublicConfiguration_success_returnsFirstConfig() {
        when(configRepo.findAll()).thenReturn(List.of(buildConfig()));

        ConfigResponse response = configService.getPublicConfiguration();

        assertThat(response.getCommerceName()).isEqualTo("MiTienda");
    }

    @Test
    void getPublicConfiguration_noConfigs_throwsRuntime() {
        when(configRepo.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> configService.getPublicConfiguration())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No config found");
    }
}