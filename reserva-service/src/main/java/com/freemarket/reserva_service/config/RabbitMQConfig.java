package com.freemarket.reserva_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE    = "reserva.exchange";
    public static final String ROUTING_KEY = "reserva.created";

    public static final String QUEUE_PENDIENTE     = "reserva.pendiente.queue";
    public static final String ROUTING_KEY_PENDIENTE = "reserva.pendiente";

    public static final String QUEUE_PENDIENTE_DLQ     = "reserva.pendiente.dlq";
    public static final String EXCHANGE_DLQ            = "reserva.dlq.exchange";
    public static final String ROUTING_KEY_DLQ         = "reserva.pendiente.dead";


    public static final String EXCHANGE_DELIVERY      = "delivery.exchange";
    public static final String QUEUE_COMPLETADO       = "delivery.completado.queue";
    public static final String ROUTING_KEY_COMPLETADO = "delivery.completado";
    
    @Bean
    public TopicExchange deliveryExchange() {
    return new TopicExchange(EXCHANGE_DELIVERY);
    }

    @Bean
    public Queue deliveryCompletadoQueue() {
    return QueueBuilder.durable(QUEUE_COMPLETADO).build();
    }

    @Bean
    public Binding bindingDeliveryCompletado() {
    return BindingBuilder
        .bind(deliveryCompletadoQueue())
        .to(deliveryExchange())
        .with(ROUTING_KEY_COMPLETADO);
    }
    
    @Bean
    public TopicExchange reservaExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public TopicExchange dlqExchange() {
        return new TopicExchange(EXCHANGE_DLQ);
    }

   
    @Bean
    public Queue reservaPendienteQueue() {
        return QueueBuilder
            .durable(QUEUE_PENDIENTE)
            .withArgument("x-dead-letter-exchange", EXCHANGE_DLQ)
            .withArgument("x-dead-letter-routing-key", ROUTING_KEY_DLQ)
            .build();
    }

    @Bean
    public Queue reservaPendienteDlq() {
        return QueueBuilder
            .durable(QUEUE_PENDIENTE_DLQ)
            .build();
    }

    @Bean
    public Binding bindingPendiente() {
        return BindingBuilder
            .bind(reservaPendienteQueue())
            .to(reservaExchange())
            .with(ROUTING_KEY_PENDIENTE);
    }

    @Bean
    public Binding bindingDlq() {
        return BindingBuilder
            .bind(reservaPendienteDlq())
            .to(dlqExchange())
            .with(ROUTING_KEY_DLQ);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
        RabbitTemplate t = new RabbitTemplate(cf);
        t.setMessageConverter(messageConverter());
        return t;
    }
}