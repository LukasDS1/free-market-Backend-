package com.freemarket.delivery_service.service;

import com.freemarket.delivery_service.messaging.DeliveryEventPublisher;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.freemarket.delivery_service.enums.DeliveryStatus;
import com.freemarket.delivery_service.model.Delivery;
import com.freemarket.delivery_service.model.DeliveryDetails;
import com.freemarket.delivery_service.repository.DeliveryDetailsRepository;
import com.freemarket.delivery_service.repository.DeliveryRepository;
import com.freemarket.delivery_service.response.DeliveryResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryEventPublisher deliveryEventPublisher;
    private final DeliveryRepository deliveryRepository;
    private final DeliveryDetailsRepository deliveryDetailsrepo;

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

        if (newStatus.equals(DeliveryStatus.ENTREGADO)) {
        delivery.getDeliveryDetails().setDeliveryEndDate(LocalDate.now());
        }

        Delivery saved = deliveryRepository.save(delivery);
        
        if(newStatus.equals(DeliveryStatus.ENTREGADO)){
            deliveryEventPublisher.publishDeliveryCompletado(saved.getDeliveryDetails().getIdReserva());
        }
        

        return toResponse(saved);
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

    public List<DeliveryResponse> getDeliveriesByUsuario(Long idUsuario) {
    return deliveryRepository.findByDeliveryDetails_IdUsuario(idUsuario)
        .stream()
        .map(this::toResponse)
        .toList();
}

  private DeliveryResponse toResponse(Delivery delivery) {
    return new DeliveryResponse(
        delivery.getIdDelivery(),
        delivery.getStatus().name(),
        delivery.getDeliveryDetails().getIdReserva(),
        delivery.getDeliveryDetails().getIdUsuario(),
        delivery.getDeliveryDetails().getIdRepartidor(),
        delivery.getDeliveryDetails().getIdDeliveryDetails(), 
        delivery.getDeliveryDetails().getDeliveryBeginDate(),
        delivery.getDeliveryDetails().getDeliveryEndDate()
    );
}
public List<DeliveryResponse> getDeliveriesByRepartidor(Long idRepartidor) {
    return deliveryDetailsrepo.findByIdRepartidor(idRepartidor)
        .stream()
        .map(details -> toResponse(details.getDelivery())) // navega Delivery via la relación
        .toList();
}
    public List<DeliveryResponse> getDeliveriesByStatus(DeliveryStatus status) {
    return deliveryRepository.findByStatus(status)
        .stream()
        .map(this::toResponse)
        .toList();
}



public List<DeliveryResponse> getAllDeliveries() {
    return deliveryRepository.findAll()
        .stream()
        .map(this::toResponse)
        .toList();
}

public void takeDelivery(Long idDeliveryDetails, Long idRepartidor){
    

    DeliveryDetails details = deliveryDetailsrepo.findById(idDeliveryDetails)
        .orElseThrow(() -> new IllegalArgumentException("DeliveryDetails no encontrado"));
    
    details.setIdRepartidor(idRepartidor);
    deliveryDetailsrepo.save(details);
    

    Delivery delivery = deliveryRepository.findByDeliveryDetails_IdDeliveryDetails(idDeliveryDetails)
        .orElseThrow(() -> new IllegalArgumentException("Delivery no encontrado"));
    
    delivery.setStatus(DeliveryStatus.EN_CAMINO);
    deliveryRepository.save(delivery);
}




}

