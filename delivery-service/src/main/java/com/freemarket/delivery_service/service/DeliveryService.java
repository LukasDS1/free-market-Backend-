package com.freemarket.delivery_service.service;

import org.springframework.stereotype.Service;

import com.freemarket.delivery_service.enums.DeliveryStatus;
import com.freemarket.delivery_service.model.Delivery;
import com.freemarket.delivery_service.repository.DeliveryRepository;
import com.freemarket.delivery_service.response.DeliveryResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    // Retorna la entidad para uso interno
    private Delivery findByReserva(Long idReserva) {
        return deliveryRepository.findByDeliveryDetails_IdReserva(idReserva)
            .orElseThrow(() -> new IllegalArgumentException("Delivery no encontrado"));
    }

    // Retorna el response para el controller
    public DeliveryResponse getReserva(Long idReserva) {
        return toResponse(findByReserva(idReserva));
    }

    public DeliveryResponse updateStatus(Long idReserva, DeliveryStatus newStatus) {
        Delivery delivery = findByReserva(idReserva);
        validateTransition(delivery.getStatus(), newStatus);

        delivery.setStatus(newStatus);
        return toResponse(deliveryRepository.save(delivery));
    }

    public void cancelDelivery(Long idReserva) {
        Delivery delivery = findByReserva(idReserva);

        if (delivery.getStatus().equals(DeliveryStatus.CANCELADO)) {
            throw new IllegalArgumentException("El delivery ya está cancelado");
        }

        if (delivery.getStatus().equals(DeliveryStatus.ENTREGADO)) {
            throw new IllegalArgumentException("No se puede cancelar un delivery ya entregado");
        }

        delivery.setStatus(DeliveryStatus.CANCELADO);
        deliveryRepository.save(delivery);
    }

    private void validateTransition(DeliveryStatus current, DeliveryStatus next) {
        if (current == DeliveryStatus.ENTREGADO || current == DeliveryStatus.CANCELADO) {
            throw new IllegalArgumentException("Estado final, no se puede cambiar");
        }
        if (current == DeliveryStatus.EN_CAMINO && next == DeliveryStatus.PENDIENTE) {
            throw new IllegalArgumentException("No puedes volver a PENDIENTE");
        }
    }

    private DeliveryResponse toResponse(Delivery delivery) {
        return new DeliveryResponse(
            delivery.getIdDelivery(),
            delivery.getStatus().name(),
            delivery.getDeliveryDetails().getIdReserva(),
            delivery.getDeliveryDetails().getDeliveryBeginDate(),
            delivery.getDeliveryDetails().getDeliveryEndDate()
        );
    }
}