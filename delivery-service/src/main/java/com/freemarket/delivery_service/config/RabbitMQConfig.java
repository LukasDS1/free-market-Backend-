package com.freemarket.delivery_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE       = "delivery.queue";
    public static final String EXCHANGE    = "reserva.exchange";
    public static final String ROUTING_KEY = "reserva.created";
    public static final String QUEUE_CANCELLED  = "delivery.cancelled.queue";
    public static final String ROUTING_KEY_CANCELLED = "reserva.cancelled";

    @Bean public Queue deliveryQueue() { return QueueBuilder.durable(QUEUE).build(); }

    @Bean public TopicExchange reservaExchange() { return new TopicExchange(EXCHANGE); }

    @Bean
    public Binding binding(Queue deliveryQueue, TopicExchange reservaExchange) {
        return BindingBuilder.bind(deliveryQueue).to(reservaExchange).with(ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Binding bindingCancelled(Queue deliveryCancelledQueue, TopicExchange reservaExchange) {
    return BindingBuilder.bind(deliveryCancelledQueue).to(reservaExchange).with(ROUTING_KEY_CANCELLED);
    }


@   Bean
    public Queue deliveryCancelledQueue() {
    return QueueBuilder.durable(QUEUE_CANCELLED).build();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
        RabbitTemplate t = new RabbitTemplate(cf);
        t.setMessageConverter(messageConverter());
        return t;
    }
}
