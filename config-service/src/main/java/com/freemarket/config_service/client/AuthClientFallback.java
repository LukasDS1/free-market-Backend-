package com.freemarket.config_service.client;

import org.springframework.stereotype.Component;

import com.freemarket.config_service.exception.ServiceUnavailableException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthClientFallback implements AuthClient {
     @Override
    public Boolean getUserById(Long id)  {
        log.warn("auth-service no disponible, ejecutando fallback para id: {}", id);
        throw new ServiceUnavailableException("Service is not available");
    }
}
