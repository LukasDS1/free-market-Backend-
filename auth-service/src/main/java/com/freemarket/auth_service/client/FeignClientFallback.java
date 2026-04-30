package com.freemarket.auth_service.client;

import org.springframework.stereotype.Component;

import com.freemarket.auth_service.exception.ServiceUnavailableException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FeignClientFallback implements FeingClient {

    @Override
    public String getStateById(Long id){
        log.warn("auth-service no disponible, ejecutando fallback para id: {}", id);
        throw new ServiceUnavailableException("Service is not available");
    }

}
