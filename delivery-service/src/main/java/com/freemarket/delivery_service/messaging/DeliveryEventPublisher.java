package com.freemarket.delivery_service.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.freemarket.delivery_service.config.RabbitMQConfig;
import com.freemarket.delivery_service.messaging.event.DeliveryCompletadoEvent;

import lombok.RequiredArgsConstructor;                  
@Component                                               
@RequiredArgsConstructor                                
public class DeliveryEventPublisher {

    private final RabbitTemplate rabbitTemplate;  

   public void publishDeliveryCompletado(Long idReserva) {
    rabbitTemplate.convertAndSend(
        RabbitMQConfig.EXCHANGE_DELIVERY,      
        RabbitMQConfig.ROUTING_KEY_COMPLETADO, 
        new DeliveryCompletadoEvent(idReserva)
    );
}
}