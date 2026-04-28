package com.freemarket.privileges_service.service;


import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.freemarket.privileges_service.exception.ServiceUnavailableException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestService {

    private final RestTemplate rest;
    
    @CircuitBreaker(name = "privilegesService", fallbackMethod = "validateRoleFallback")
    public Boolean validateRoleExists(Long roleId) {
        String URL = "http://auth-service/api-v1/auth/role/{roleId}";
        return rest.getForObject(URL, Boolean.class, roleId);
    }

    public Boolean validateRoleFallback(Long roleId, Exception ex) {
        throw new ServiceUnavailableException("Service is not available");
    }
    

}
