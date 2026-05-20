package com.freemarket.delivery_service.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.freemarket.delivery_service.enums.DeliveryStatus;
import com.freemarket.delivery_service.model.Delivery;
import com.freemarket.delivery_service.model.DeliveryDetails;
import com.freemarket.delivery_service.response.DeliveryResponse;
import com.freemarket.delivery_service.service.DeliveryService;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(DeliveryController.class)
public class DeliveryControllerTest {

    @MockitoBean
    private DeliveryService deliveryService;

    @Autowired
    private MockMvc mockMvc;


    // ── Helpers ───────────────────────────────────────────────────────────────

    private Delivery buildDelivery() {
        DeliveryDetails details = new DeliveryDetails();
        details.setIdReserva(1L);

        Delivery delivery = new Delivery();
        delivery.setIdDelivery(1L);
        delivery.setStatus(DeliveryStatus.PENDIENTE);
        delivery.setDeliveryDetails(details);
        return delivery;
    }


    // ── GET /reserva/{idReserva} ──────────────────────────────────────────────
    @Test
void getDelivery_success_returnsOk() throws Exception {
    when(deliveryService.getReserva(1L)).thenReturn(buildDeliveryResponse());

    mockMvc.perform(get("/api-v1/delivery/reserva/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.idDelivery").value(1L))
            .andExpect(jsonPath("$.status").value("PENDIENTE"));
}

// Helper para construir el response en los tests
private DeliveryResponse buildDeliveryResponse() {
    return new DeliveryResponse(
        1L,
        "PENDIENTE",
        10L,
        1L,
        LocalDate.of(2026, 5, 7),
        LocalDate.of(2026, 5, 10)
    );
}


    // ── PATCH /reserva/{idReserva}/status ─────────────────────────────────────

    @Test
    void updateStatus_success_returnsUpdatedDelivery() throws Exception {
        DeliveryResponse updated = buildDeliveryResponse();
        updated.setStatus("EN_CAMINO");

        when(deliveryService.updateStatus(1L, DeliveryStatus.EN_CAMINO)).thenReturn(updated);

        mockMvc.perform(patch("/api-v1/delivery/reserva/status/1") 
        .param("status", "EN_CAMINO"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("EN_CAMINO"));
    }


}