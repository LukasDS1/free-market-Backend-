package com.freemarket.delivery_service.messaging;

import java.time.LocalDate;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.freemarket.delivery_service.config.RabbitMQConfig;
import com.freemarket.delivery_service.enums.DeliveryStatus;
import com.freemarket.delivery_service.messaging.event.ReservaCancelledEvent;
import com.freemarket.delivery_service.messaging.event.ReservaCreatedEvent;
import com.freemarket.delivery_service.model.Delivery;
import com.freemarket.delivery_service.model.DeliveryDetails;
import com.freemarket.delivery_service.repository.DeliveryDetailsRepository;
import com.freemarket.delivery_service.repository.DeliveryRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional
public class DeliveryEventListener {

    private final DeliveryDetailsRepository deliveryDetailsRepository;
    private final DeliveryRepository deliveryRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handleReservaCreated(ReservaCreatedEvent event){
        if(deliveryDetailsRepository.existsByIdReserva(event.getIdReserva())){
            return;
        }


        DeliveryDetails details = new DeliveryDetails();
        details.setIdReserva(event.getIdReserva());
        details.setDeliveryBeginDate(LocalDate.now());
        details.setDeliveryEndDate(LocalDate.now().plusDays(3));
        DeliveryDetails savedDetails = deliveryDetailsRepository.save(details);

        Delivery delivery = new Delivery();
        delivery.setStatus(DeliveryStatus.PENDIENTE);
        delivery.setDeliveryDetails(savedDetails);
        deliveryRepository.save(delivery);
    

    }

    @RabbitListener(queues = "delivery.cancelled.queue")
    public void handleReservaCancelled(ReservaCancelledEvent event) {
    DeliveryDetails details = deliveryDetailsRepository.findByIdReserva(event.getIdReserva())
        .orElse(null);
    if (details == null) return;
 deliveryRepository.findByDeliveryDetails_IdReserva(event.getIdReserva())
        .ifPresent(delivery -> {
            if (!delivery.getStatus().equals(DeliveryStatus.ENTREGADO)) {
                delivery.setStatus(DeliveryStatus.CANCELADO);
                deliveryRepository.save(delivery);
            }
        });
}



}
