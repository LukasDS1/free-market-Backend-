package com.freemarket.config_service.messaging;

import com.freemarket.config_service.client.AuthClient;
import com.freemarket.config_service.config.RabbitMQConfig;
import com.freemarket.config_service.messaging.event.ConfigPendienteEvent;
import com.freemarket.config_service.model.Configuration;
import com.freemarket.config_service.repository.ConfigRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import java.sql.Date;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfigPendienteConsumer {

    private final RabbitTemplate rabbitTemplate;
    private final AuthClient            authClient;
    private final ConfigRepository      configRepo;
    private final CircuitBreakerRegistry circuitBreakerRegistry;


    @RabbitListener(queues = RabbitMQConfig.QUEUE_PENDIENTE)
    public void procesarConfigPendiente(ConfigPendienteEvent event){
        log.info("Procesando config pendiente");

        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("auth-service");
        if(cb.getState() == CircuitBreaker.State.OPEN){
            log.warn("circuito abierto ,reintentado");
            throw new RuntimeException("auth-service no disponible, reintentando después");
        }

        try{


            Boolean existe = authClient.getUserById(event.getIdUser());

            if(existe == null){
                   log.warn("auth-service caído, reencolando idUser={}", event.getIdUser());
            rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE,
            RabbitMQConfig.ROUTING_KEY_RETRY,
            event
            );
            return;
            }

           if (!existe) {
           log.warn("Usuario no existe, descartando idUser={}", event.getIdUser());
          return;
           }   


            if(event.getOperationType() == ConfigPendienteEvent.OperationType.CREATE){
                procesarCreate(event);
            }else{
                procesarUpdate(event);
            }
        } catch(Exception e){
            log.warn("Error procesando la cola, reintentado");
            throw new AmqpRejectAndDontRequeueException ("error",e);
        }
    }

     private void procesarCreate(ConfigPendienteEvent event) {
        if (configRepo.existsByIdUser(event.getIdUser())) {
            log.warn("Config ya existe, descartando CREATE → idUser={}", event.getIdUser());
            return;
        }
        configRepo.save(toEntity(event));
        log.info("Config CREADA desde cola → idUser={}", event.getIdUser());
    }

      private void procesarUpdate(ConfigPendienteEvent event) {
        configRepo.findByIdUser(event.getIdUser()).ifPresentOrElse(
            config -> {
                applyUpdates(config, event);
                configRepo.save(config);
                log.info("Config ACTUALIZADA desde cola → idUser={}", event.getIdUser());
            },
            () -> log.warn("No existe config para UPDATE → idUser={}", event.getIdUser())
        );
    }

    private Configuration toEntity(ConfigPendienteEvent event) {
        Configuration c = new Configuration();
        c.setIdUser(event.getIdUser());
        c.setCommerceName(event.getCommerceName());
        c.setLogoUrl(event.getLogoUrl());
        c.setFavicomUrl(event.getFavicomUrl());
        c.setPrimarColor(event.getPrimaryColor());
        c.setSecondaryColor(event.getSecondaryColor());
        c.setPrincipalfont(event.getPrincipalFont());
        c.setUpdateAt(Date.valueOf(LocalDate.now()));
        return c;
    }

    private void applyUpdates(Configuration c, ConfigPendienteEvent event) {
        if (event.getCommerceName()   != null) c.setCommerceName(event.getCommerceName());
        if (event.getLogoUrl()        != null) c.setLogoUrl(event.getLogoUrl());
        if (event.getFavicomUrl()     != null) c.setFavicomUrl(event.getFavicomUrl());
        if (event.getPrimaryColor()   != null) c.setPrimarColor(event.getPrimaryColor());
        if (event.getSecondaryColor() != null) c.setSecondaryColor(event.getSecondaryColor());
        if (event.getPrincipalFont()  != null) c.setPrincipalfont(event.getPrincipalFont());
        c.setUpdateAt(Date.valueOf(LocalDate.now()));
    }

}
