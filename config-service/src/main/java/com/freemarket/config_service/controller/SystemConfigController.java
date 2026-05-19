package com.freemarket.config_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freemarket.config_service.model.SystemConfig;
import com.freemarket.config_service.service.SystemConfigService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api-v1/config/system")
@RequiredArgsConstructor
@Tag(name = "System Config Controller", description = "Configuración global del sistema")
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    @GetMapping("/country")
    @Operation(summary = "Obtener país de búsqueda actual")
    public ResponseEntity<SystemConfig> getCountry() {
        return ResponseEntity.ok(systemConfigService.getCountry());
    }

    @PatchMapping("/country/{countryCode}/{countryName}")    
    @Operation(summary = "Actualizar país de búsqueda",description = "Cambia el país donde Nominatim buscará las ubicaciones. Usar código ISO 3166-1 alpha-2 (ej: cl, ar, pe)")
    public ResponseEntity<SystemConfig> updateCountry(
        @PathVariable String countryCode,
        @PathVariable String countryName) {
    return ResponseEntity.ok(systemConfigService.updateCountry(countryCode, countryName));
}

}