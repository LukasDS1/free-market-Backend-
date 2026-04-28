package com.freemarket.config_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.freemarket.config_service.exception.ServiceUnavailableException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class RestService {

    private final RestTemplate rest;

    
    @CircuitBreaker(name = "reservaService", fallbackMethod = "getUserByIdFallback")
    public Boolean getUserById(Long id) {
        String URL = "http://auth-service/api-v1/auth/{id}";
        return rest.getForObject(URL, Boolean.class, id);
    }

    public Boolean getUserByIdFallback(Exception ex) {
        log.warn (ex.getMessage());
        throw new ServiceUnavailableException("Service is not avalible");
    }

}
