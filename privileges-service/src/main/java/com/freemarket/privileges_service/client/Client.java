package com.freemarket.privileges_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service",fallback = ClientFallback.class)
public interface Client {
    @GetMapping("/api-v1/auth/role/{roleId}")
    Boolean getRoleById(@PathVariable("roleId") Long roleId);


}
    