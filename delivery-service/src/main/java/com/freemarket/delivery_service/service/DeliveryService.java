package com.freemarket.delivery_service.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.freemarket.delivery_service.enums.DeliveryStatus;
import com.freemarket.delivery_service.exception.NotFoundException;
import com.freemarket.delivery_service.messaging.DeliveryEventPublisher;
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

    private Delivery findByReserva(Long idReserva) {
        return deliveryRepository.findByDeliveryDetails_IdReserva(idReserva)
            .orElseThrow(() -> new NotFoundException("Delivery not found"));
    }

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

        if (newStatus.equals(DeliveryStatus.ENTREGADO)) {
            deliveryEventPublisher.publishDeliveryCompletado(saved.getDeliveryDetails().getIdReserva());
        }

        return toResponse(saved);
    }

    public void cancelDelivery(Long idReserva) {
        Delivery delivery = findByReserva(idReserva);

        if (delivery.getStatus().equals(DeliveryStatus.CANCELADO))
            throw new IllegalArgumentException("Delivery is already cancelled");

        if (delivery.getStatus().equals(DeliveryStatus.ENTREGADO))
            throw new IllegalArgumentException("Cannot cancel a delivery that has already been delivered");

        delivery.setStatus(DeliveryStatus.CANCELADO);
        deliveryRepository.save(delivery);
    }

    private void validateTransition(DeliveryStatus current, DeliveryStatus next) {
        if (current == DeliveryStatus.ENTREGADO || current == DeliveryStatus.CANCELADO)
            throw new IllegalArgumentException("This delivery has reached a final status and cannot be changed");

        if (current == DeliveryStatus.EN_CAMINO && next == DeliveryStatus.PENDIENTE)
            throw new IllegalArgumentException("cannot go back to PENDIENTE once the delivery is EN_CAMINO");
    }

    public List<DeliveryResponse> getDeliveriesByUsuario(Long idUsuario) {
        return deliveryRepository.findByDeliveryDetails_IdUsuario(idUsuario)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public List<DeliveryResponse> getDeliveriesByRepartidor(Long idRepartidor) {
        return deliveryDetailsrepo.findByIdRepartidor(idRepartidor)
            .stream()
            .map(details -> toResponse(details.getDelivery()))
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

    public void takeDelivery(Long idDeliveryDetails, Long idRepartidor) {
        DeliveryDetails details = deliveryDetailsrepo.findById(idDeliveryDetails)
            .orElseThrow(() -> new NotFoundException("DeliveryDetails not found"));

        details.setIdRepartidor(idRepartidor);
        deliveryDetailsrepo.save(details);

        Delivery delivery = deliveryRepository.findByDeliveryDetails_IdDeliveryDetails(idDeliveryDetails)
            .orElseThrow(() -> new NotFoundException("Delivery not found"));

        delivery.setStatus(DeliveryStatus.EN_CAMINO);
        deliveryRepository.save(delivery);
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
}