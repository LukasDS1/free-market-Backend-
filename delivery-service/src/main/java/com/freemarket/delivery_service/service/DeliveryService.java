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
        delivery.setStatus(newStatus);
        return deliveryRepository.save(delivery);
    }


}
