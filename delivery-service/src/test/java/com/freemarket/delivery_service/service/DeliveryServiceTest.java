package com.freemarket.delivery_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.freemarket.delivery_service.enums.DeliveryStatus;
import com.freemarket.delivery_service.exception.NotFoundException;
import com.freemarket.delivery_service.model.Delivery;
import com.freemarket.delivery_service.model.DeliveryDetails;
import com.freemarket.delivery_service.repository.DeliveryRepository;
import com.freemarket.delivery_service.response.DeliveryResponse;

@ExtendWith(MockitoExtension.class)
public class DeliveryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @InjectMocks
    private DeliveryService deliveryService;


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


    // ── getReserva ────────────────────────────────────────────────────────────

    @Test
    void getReserva_success_returnsDelivery() {
        when(deliveryRepository.findByDeliveryDetails_IdReserva(1L))
                .thenReturn(Optional.of(buildDelivery()));

        DeliveryResponse result = deliveryService.getReserva(1L);

        assertThat(result.getIdDelivery()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo("PENDIENTE");
    }

    @Test
void getReserva_notFound_throwsNotFoundException() {
    when(deliveryRepository.findByDeliveryDetails_IdReserva(99L))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() -> deliveryService.getReserva(99L))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("Delivery not found");
}


    // ── updateStatus ──────────────────────────────────────────────────────────

    @Test
    void updateStatus_success_returnsUpdatedDelivery() {
        Delivery delivery = buildDelivery();
        Delivery saved = buildDelivery();
        saved.setStatus(DeliveryStatus.EN_CAMINO);

        when(deliveryRepository.findByDeliveryDetails_IdReserva(1L)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(saved);

        DeliveryResponse result = deliveryService.updateStatus(1L, DeliveryStatus.EN_CAMINO);

        assertThat(result.getStatus()).isEqualTo("EN_CAMINO");
    }

    @Test
void updateStatus_deliveryNotFound_throwsNotFoundException() {
    when(deliveryRepository.findByDeliveryDetails_IdReserva(99L))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() -> deliveryService.updateStatus(99L, DeliveryStatus.EN_CAMINO))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("Delivery not found");
}
}