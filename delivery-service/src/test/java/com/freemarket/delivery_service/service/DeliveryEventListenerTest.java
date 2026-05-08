package com.freemarket.delivery_service.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.freemarket.delivery_service.enums.DeliveryStatus;
import com.freemarket.delivery_service.messaging.DeliveryEventListener;
import com.freemarket.delivery_service.messaging.event.ReservaCancelledEvent;
import com.freemarket.delivery_service.messaging.event.ReservaCreatedEvent;
import com.freemarket.delivery_service.model.Delivery;
import com.freemarket.delivery_service.model.DeliveryDetails;
import com.freemarket.delivery_service.repository.DeliveryDetailsRepository;
import com.freemarket.delivery_service.repository.DeliveryRepository;

@ExtendWith(MockitoExtension.class)
public class DeliveryEventListenerTest {

    @Mock
    private DeliveryDetailsRepository deliveryDetailsRepository;

    @Mock
    private DeliveryRepository deliveryRepository;

    @InjectMocks
    private DeliveryEventListener deliveryEventListener;



    private DeliveryDetails buildSavedDetails() {
        DeliveryDetails details = new DeliveryDetails();
        details.setIdDeliveryDetails(1L);
        details.setIdReserva(1L);
        return details;
    }

    private Delivery buildDelivery(DeliveryDetails details) {
        Delivery delivery = new Delivery();
        delivery.setIdDelivery(1L);
        delivery.setStatus(DeliveryStatus.PENDIENTE);
        delivery.setDeliveryDetails(details);
        return delivery;
    }


    // ── handleReservaCreated ──────────────────────────────────────────────────

    @Test
    void handleReservaCreated_success_createsDeliveryAndDetails() {
        DeliveryDetails savedDetails = buildSavedDetails();

        when(deliveryDetailsRepository.existsByIdReserva(1L)).thenReturn(false);
        when(deliveryDetailsRepository.save(any(DeliveryDetails.class))).thenReturn(savedDetails);

        deliveryEventListener.handleReservaCreated(new ReservaCreatedEvent(1L));

        verify(deliveryDetailsRepository).save(any(DeliveryDetails.class));
        verify(deliveryRepository).save(any(Delivery.class));
    }

    @Test
    void handleReservaCreated_alreadyExists_skipsCreation() {
        when(deliveryDetailsRepository.existsByIdReserva(1L)).thenReturn(true);

        deliveryEventListener.handleReservaCreated(new ReservaCreatedEvent(1L));

        verify(deliveryDetailsRepository, never()).save(any());
        verify(deliveryRepository, never()).save(any());
    }


    // ── handleReservaCancelled ────────────────────────────────────────────────

   @Test
void handleReservaCancelled_success_deletesDeliveryAndDetails() {
    DeliveryDetails details = buildSavedDetails();
    Delivery delivery = buildDelivery(details);

    when(deliveryDetailsRepository.findByIdReserva(1L)).thenReturn(Optional.of(details));
    when(deliveryRepository.findByDeliveryDetails_IdReserva(1L)).thenReturn(Optional.of(delivery));

    deliveryEventListener.handleReservaCancelled(new ReservaCancelledEvent(1L));

    verify(deliveryRepository).save(delivery);
    verify(deliveryDetailsRepository, never()).delete(any());
}

@Test
void handleReservaCancelled_deliveryNotFound_onlyDeletesDetails() {
    DeliveryDetails details = buildSavedDetails();

    when(deliveryDetailsRepository.findByIdReserva(1L)).thenReturn(Optional.of(details));
    when(deliveryRepository.findByDeliveryDetails_IdReserva(1L)).thenReturn(Optional.empty());

    deliveryEventListener.handleReservaCancelled(new ReservaCancelledEvent(1L));

    verify(deliveryRepository, never()).save(any());
    verify(deliveryDetailsRepository, never()).delete(any());
}

    @Test
    void handleReservaCancelled_detailsNotFound_doesNothing() {
        when(deliveryDetailsRepository.findByIdReserva(1L)).thenReturn(Optional.empty());

        deliveryEventListener.handleReservaCancelled(new ReservaCancelledEvent(1L));

        verify(deliveryRepository, never()).delete(any());
        verify(deliveryDetailsRepository, never()).delete(any());
    }

    
}