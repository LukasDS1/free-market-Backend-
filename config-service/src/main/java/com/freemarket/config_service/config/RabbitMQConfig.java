package com.freemarket.config_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE            = "config.exchange";
    public static final String QUEUE_PENDIENTE     = "config.pendiente.queue";
    public static final String ROUTING_KEY_PENDIENTE = "config.pendiente";



    public static final String EXCHANGE_DLQ        = "config.dlq.exchange";
    public static final String QUEUE_PENDIENTE_DLQ = "config.pendiente.dlq";
    public static final String ROUTING_KEY_DLQ     = "config.pendiente.dead";

    public static final String QUEUE_RETRY = "config.retry.queue";
    public static final String ROUTING_KEY_RETRY = "config.retry";

    public static final int RETRY_TTL = 30000; 

    @Bean
    public TopicExchange configExchange(){
        return new TopicExchange(EXCHANGE);

    }

    @Bean
    public TopicExchange configDlqExchange(){
        return new TopicExchange(EXCHANGE_DLQ);

    }

    @Bean
    public Queue configRetryQueue() {
    return QueueBuilder
            .durable(QUEUE_RETRY)
            .withArgument("x-message-ttl", RETRY_TTL)
            .withArgument("x-dead-letter-exchange", EXCHANGE)
            .withArgument("x-dead-letter-routing-key", ROUTING_KEY_PENDIENTE)
            .build();
    }

     @Bean
    public Queue configPendienteQueue() {
        return QueueBuilder
                .durable(QUEUE_PENDIENTE)
                .withArgument("x-dead-letter-exchange", EXCHANGE_DLQ)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY_DLQ)
                .build();
    }
        @Bean
    public Queue configPendienteDlq() {
        return QueueBuilder
                .durable(QUEUE_PENDIENTE_DLQ)
                .build();
    }

        @Bean
    public Binding bindingPendiente() {
        return BindingBuilder
                .bind(configPendienteQueue())
                .to(configExchange())
                .with(ROUTING_KEY_PENDIENTE);
    }

        @Bean
    public Binding bindingDlq() {
        return BindingBuilder
                .bind(configPendienteDlq())
                .to(configDlqExchange())
                .with(ROUTING_KEY_DLQ);
    }

    @Bean
    public Binding bindingRetry() {
    return BindingBuilder
            .bind(configRetryQueue())
            .to(configExchange())
            .with(ROUTING_KEY_RETRY);
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
