package com.freemarket.auth_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestService {

    private final RestTemplate restTemplate;

    
    @CircuitBreaker(name = "authService", fallbackMethod = "getStateFallback")
    public String getState(Long userId) {
        String URL = "http://state-service/api-v1/state/{id}";
        return restTemplate.getForObject(URL, String.class, userId);
    }

    public String getStateFallback(Long userId, Exception ex) {
        log.warn(" ex.getMessage()");
        return "State is not available";
    }


    

}
