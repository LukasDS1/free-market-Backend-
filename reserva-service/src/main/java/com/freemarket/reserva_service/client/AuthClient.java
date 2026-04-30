package com.freemarket.reserva_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service", fallback = AuthClientFallback.class)
public interface AuthClient {
    @GetMapping("/api-v1/auth/{id}")
    Boolean getUserById(@PathVariable("id") Long id);

}
