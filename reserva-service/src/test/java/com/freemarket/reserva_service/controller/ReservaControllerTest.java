package com.freemarket.reserva_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.freemarket.reserva_service.exception.GlobalExceptionHandler;
import com.freemarket.reserva_service.exception.ServiceUnavailableException;
import com.freemarket.reserva_service.request.CancelReserveRequest;
import com.freemarket.reserva_service.request.ReserveRequest;
import com.freemarket.reserva_service.response.ReservaResponse;
import com.freemarket.reserva_service.service.ReservaService;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ReservaController.class)
@Import(GlobalExceptionHandler.class)
public class ReservaControllerTest {

    @MockitoBean
    private ReservaService reservaService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    private ReservaResponse buildResponse() {
        ReservaResponse res = new ReservaResponse();
        res.setIdReserva(1L);
        res.setTotalPrice(2000);
        return res;
    }

    @Test
void createReserve_success_returnsOk() throws Exception {
    when(reservaService.createReserva(any(ReserveRequest.class), any(String.class)))
            .thenReturn(buildResponse()); // ← any() para el key

    mockMvc.perform(post("/api-v1/reserve/createReserve")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Idempotency-Key", "test-key-123") // ← header
                    .content(objectMapper.writeValueAsString(new ReserveRequest())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.idReserva").value(1L))
            .andExpect(jsonPath("$.totalPrice").value(2000));
}

@Test
void createReserve_userNotFound_returns400() throws Exception {
    when(reservaService.createReserva(any(ReserveRequest.class), any(String.class)))
            .thenThrow(new IllegalArgumentException("User not Found"));

    mockMvc.perform(post("/api-v1/reserve/createReserve")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Idempotency-Key", "test-key-123")
                    .content(objectMapper.writeValueAsString(new ReserveRequest())))
            .andExpect(status().isBadRequest());
}

@Test
void createReserve_serviceUnavailable_returns503() throws Exception {
    when(reservaService.createReserva(any(ReserveRequest.class), any(String.class)))
            .thenThrow(new ServiceUnavailableException("Service is not available"));

    mockMvc.perform(post("/api-v1/reserve/createReserve")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Idempotency-Key", "test-key-123")
                    .content(objectMapper.writeValueAsString(new ReserveRequest())))
            .andExpect(status().isServiceUnavailable());
}

// ← test nuevo para idempotencia
@Test
void createReserve_withoutKey_generatesKeyAutomatically() throws Exception {
    when(reservaService.createReserva(any(ReserveRequest.class), any(String.class)))
            .thenReturn(buildResponse());

    mockMvc.perform(post("/api-v1/reserve/createReserve") // ← sin header
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new ReserveRequest())))
            .andExpect(status().isOk());
}


}
