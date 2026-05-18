package com.freemarket.reserva_service.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.freemarket.reserva_service.config.RabbitMQConfig;
import com.freemarket.reserva_service.messaging.event.ReservaPendienteEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
@RequiredArgsConstructor
public class ReservaPendienteProducer {

    private final RabbitTemplate rabbitTemplate;

    public void enviarReservaPendiente(Long idReserva, Long idUser) {
        ReservaPendienteEvent event = new ReservaPendienteEvent(idReserva, idUser);

        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE,               // exchange principal
            RabbitMQConfig.ROUTING_KEY_PENDIENTE,  // routing key
            event
        );

        log.info("📨 Reserva {} enviada a cola pendiente para usuario {}", idReserva, idUser);
    }
}
