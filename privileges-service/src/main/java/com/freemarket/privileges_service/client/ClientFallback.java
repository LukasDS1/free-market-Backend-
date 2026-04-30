package com.freemarket.privileges_service.client;

import org.springframework.stereotype.Component;

import com.freemarket.privileges_service.exception.ServiceUnavailableException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ClientFallback implements Client {
    
    @Override
    public Boolean getRoleById(Long roleId){
        log.warn("auth-service no disponible, ejecutando fallback para id: {}", roleId);
        throw new ServiceUnavailableException("Service is not available");
    }

}
