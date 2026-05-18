package com.freemarket.config_service.messaging;

import com.freemarket.config_service.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.freemarket.config_service.messaging.event.ConfigPendienteEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfigPendienteProducer {

    private final RabbitTemplate rabbitTemplate;


    public void enviarConfigPendiente(ConfigPendienteEvent event){
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE,
            RabbitMQConfig.ROUTING_KEY_PENDIENTE,
            event
        );

        log.info("Config enviada a la cola",event.getIdUser());
        
    }

}
