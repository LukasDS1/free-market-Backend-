package com.freemarket.delivery_service.service;

import org.springframework.stereotype.Service;

import com.freemarket.delivery_service.enums.DeliveryStatus;
import com.freemarket.delivery_service.model.Delivery;
import com.freemarket.delivery_service.repository.DeliveryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public Delivery getReserva(Long idReserva){
        return deliveryRepository.findByDeliveryDetails_IdReserva(idReserva).orElseThrow(() -> new IllegalArgumentException("Delivery no encontrado"));
    }

     public Delivery updateStatus(Long idReserva, DeliveryStatus newStatus) {
        Delivery delivery = getReserva(idReserva);
        validateTransition(delivery.getStatus(), newStatus);

        delivery.setStatus(newStatus);
        return deliveryRepository.save(delivery);
    }

      public void cancelDelivery(Long idReserva) {
        Delivery delivery = getReserva(idReserva);

        if(delivery.getStatus().equals(DeliveryStatus.CANCELADO)){
            throw new IllegalArgumentException("El delivery ya está cancelado");
        }

        if(delivery.getStatus().equals(DeliveryStatus.ENTREGADO)){
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


}
