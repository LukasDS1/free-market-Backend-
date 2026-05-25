package com.freemarket.config_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freemarket.config_service.exception.ServiceUnavailableException;
import com.freemarket.config_service.request.ConfigRequest;
import com.freemarket.config_service.response.ConfigResponse;
import com.freemarket.config_service.service.configService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
 
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
 
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ConfigController.class)
public class ConfigControllerTest {

    @MockitoBean
    private configService configService;
 
    @Autowired
    private MockMvc mockMvc;
 
    @Autowired
    private ObjectMapper objectMapper;


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
 
    private ConfigResponse buildResponse() {
        ConfigResponse res = new ConfigResponse();
        res.setCommerceName("MiTienda");
        res.setLogoUrl("http://logo.com");
        res.setFavicomUrl("http://favicon.com");
        res.setPrimaryColor("#FFFFFF");
        res.setSecondaryColor("#000000");
        res.setPrincipalFont("Arial");
        return res;
    }
 
    @Test
    void createConfig_success_returns201() throws Exception {
        when(configService.createConfiguration(any(ConfigRequest.class))).thenReturn(buildResponse());
 
        mockMvc.perform(post("/api-v1/config/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commerceName").value("MiTienda"));
    }
 
    @Test
    void createConfig_userNotFound_returns400() throws Exception {
        when(configService.createConfiguration(any(ConfigRequest.class)))
                .thenThrow(new IllegalArgumentException());
 
        mockMvc.perform(post("/api-v1/config/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isBadRequest());
    }
 
    @Test
    void createConfig_serviceUnavailable_returns503() throws Exception {
        when(configService.createConfiguration(any(ConfigRequest.class)))
                .thenThrow(new ServiceUnavailableException("Service is not avalible"));
 
        mockMvc.perform(post("/api-v1/config/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isServiceUnavailable());
    }
 
 
    @Test
    void updateConfig_success_returns200() throws Exception {
        ConfigResponse updated = buildResponse();
        updated.setCommerceName("NuevoNombre");
 
        when(configService.updateConfiguration(eq(1L), any(ConfigRequest.class))).thenReturn(updated);
 
        mockMvc.perform(patch("/api-v1/config/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commerceName").value("NuevoNombre"));
    }
 


    @Test
    void getConfig_success_returns200() throws Exception {
        when(configService.getConfigurationByIdUser(1L)).thenReturn(buildResponse());
 
        mockMvc.perform(get("/api-v1/config/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commerceName").value("MiTienda"));
    }
 

}
