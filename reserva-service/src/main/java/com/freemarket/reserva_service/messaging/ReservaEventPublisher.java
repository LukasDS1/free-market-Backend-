package com.freemarket.reserva_service.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.freemarket.reserva_service.config.RabbitMQConfig;
import com.freemarket.reserva_service.messaging.event.ReservaCancelledEvent;
import com.freemarket.reserva_service.messaging.event.ReservaCreatedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservaEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishReservaCreated(Long idReserva) {
        ReservaCreatedEvent event = new ReservaCreatedEvent(idReserva);
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE,
            RabbitMQConfig.ROUTING_KEY,
            event
        );
    }

    public void publishReservaCancelled(Long idReserva) {
    ReservaCancelledEvent event = new ReservaCancelledEvent(idReserva);
    rabbitTemplate.convertAndSend(
        RabbitMQConfig.EXCHANGE,
        "reserva.cancelled",  
        event
    );
}


}
