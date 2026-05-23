package com.freemarket.locations_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freemarket.locations_service.excepcion.GlobalExceptionHandler;
import com.freemarket.locations_service.excepcion.ServiceUnavailableException;
import com.freemarket.locations_service.request.LocationRequest;
import com.freemarket.locations_service.response.LocationResponse;
import com.freemarket.locations_service.response.LocationResponseForId;
import com.freemarket.locations_service.service.LocationsService;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(LocationController.class)
@Import(GlobalExceptionHandler.class)
public class LocationControllerTest {

    @MockitoBean private LocationsService locationsService;
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private static final Long USER_ID = 1L;

    private LocationRequest buildRequest() {
        LocationRequest req = new LocationRequest();
        req.setUserId(USER_ID);
        req.setStreet("Calle Apolo");
        req.setStreetNumber("3009");
        req.setComuna("Santiago");
        req.setRegion("Metropolitana");
        return req;
    }

    private LocationResponse buildResponse() {
        LocationResponse res = new LocationResponse();
        res.setLocationId(1L);
        res.setUserId(USER_ID);
        res.setStreet("Calle Apolo");
        res.setStreetNumber("3009");
        res.setStreetAddress("3009 Calle Apolo, Santiago");
        res.setLatitude(-33.45);
        res.setLongitude(-70.65);
        res.setComunaNombre("Santiago");
        res.setRegionNombre("Metropolitana");
        return res;
    }

    // ─── createLocation ───────────────────────────────────────────────────────

    @Test
    void createLocation_success_returnsOk() throws Exception {
        when(locationsService.createUserLocation(any(LocationRequest.class))).thenReturn(buildResponse());

        mockMvc.perform(post("/api-v1/location/createLocation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", USER_ID)              // ← requerido
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.street").value("Calle Apolo"))
                .andExpect(jsonPath("$.streetNumber").value("3009"))
                .andExpect(jsonPath("$.comunaNombre").value("Santiago"));
    }

    @Test
    void createLocation_userNotFound_returns400() throws Exception {
        when(locationsService.createUserLocation(any(LocationRequest.class)))
                .thenThrow(new IllegalArgumentException("Usuario no encontrado"));

        mockMvc.perform(post("/api-v1/location/createLocation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", USER_ID)              // ← requerido
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createLocation_serviceUnavailable_returns503() throws Exception {
        when(locationsService.createUserLocation(any(LocationRequest.class)))
                .thenThrow(new ServiceUnavailableException("Service is unavailable"));

        mockMvc.perform(post("/api-v1/location/createLocation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", USER_ID)              // ← requerido
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isServiceUnavailable());
    }

    // ─── updateLocation ───────────────────────────────────────────────────────

    @Test
    void updateLocation_success_returnsOk() throws Exception {
        when(locationsService.updateLocation(any(LocationRequest.class))).thenReturn(buildResponse());

        mockMvc.perform(put("/api-v1/location/updateLocation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", USER_ID)              // ← requerido
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.streetAddress").value("3009 Calle Apolo, Santiago"))
                .andExpect(jsonPath("$.street").value("Calle Apolo"))
                .andExpect(jsonPath("$.streetNumber").value("3009"));
    }

    @Test
    void updateLocation_locationNotFound_returns400() throws Exception {
        when(locationsService.updateLocation(any(LocationRequest.class)))
                .thenThrow(new IllegalArgumentException("Locacion not Found"));

        mockMvc.perform(put("/api-v1/location/updateLocation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", USER_ID)              // ← requerido
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateLocation_serviceUnavailable_returns503() throws Exception {
        when(locationsService.updateLocation(any(LocationRequest.class)))
                .thenThrow(new ServiceUnavailableException("Service is unavailable"));

        mockMvc.perform(put("/api-v1/location/updateLocation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", USER_ID)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isServiceUnavailable());
    }

    // ─── getLocationByUserId ──────────────────────────────────────────────────

    @Test
    void getLocationByUserId_success_returnsOk() throws Exception {
        LocationResponseForId responseForId = new LocationResponseForId();
        when(locationsService.getLocationByUserId(USER_ID)).thenReturn(responseForId);

        mockMvc.perform(get("/api-v1/location/getLocation/{id}", USER_ID))
                .andExpect(status().isOk());
    }

    @Test
    void getLocationByUserId_notFound_returns400() throws Exception {
        when(locationsService.getLocationByUserId(USER_ID))
                .thenThrow(new IllegalArgumentException("Usuario o ubicación no encontrada"));

        mockMvc.perform(get("/api-v1/location/getLocation/{id}", USER_ID))
                .andExpect(status().isBadRequest());
    }
}