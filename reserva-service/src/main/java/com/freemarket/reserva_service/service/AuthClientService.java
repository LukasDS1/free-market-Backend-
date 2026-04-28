package com.freemarket.reserva_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.freemarket.reserva_service.exception.ServiceUnavailableException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthClientService {

    private final RestTemplate restTemplate;

    @CircuitBreaker(name = "reservaService", fallbackMethod = "getUserByIdFallback")
    public Boolean getUserById(Long id) {
        String URL = "http://auth-service/api-v1/auth/{id}";
        return restTemplate.getForObject(URL, Boolean.class, id);
    }

    public Boolean getUserByIdFallback(Exception ex) {
        log.warn (ex.getMessage());
        throw new ServiceUnavailableException("Service is not avalible");
    }

}
