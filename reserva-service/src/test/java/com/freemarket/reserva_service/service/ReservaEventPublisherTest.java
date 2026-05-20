package com.freemarket.reserva_service.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.freemarket.reserva_service.config.RabbitMQConfig;
import com.freemarket.reserva_service.messaging.ReservaEventPublisher;
import com.freemarket.reserva_service.messaging.event.ReservaCancelledEvent;
import com.freemarket.reserva_service.messaging.event.ReservaCreatedEvent;

@ExtendWith(MockitoExtension.class)
public class ReservaEventPublisherTest {

     @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ReservaEventPublisher eventPublisher;


    @Test
    void publishReservaCreated_sendsCorrectEventToExchange() {
        eventPublisher.publishReservaCreated(1L,1L);

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.EXCHANGE),
                eq(RabbitMQConfig.ROUTING_KEY),
                any(ReservaCreatedEvent.class));
    }

    @Test
    void publishReservaCancelled_sendsCorrectEventToExchange() {
        eventPublisher.publishReservaCancelled(1L);

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.EXCHANGE),
                eq("reserva.cancelled"),
                any(ReservaCancelledEvent.class));
    }

}
