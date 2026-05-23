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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api-v1/config/system")
@RequiredArgsConstructor
@Tag(name = "System Config Controller", description = "Endpoints para la configuración global del sistema")
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    @GetMapping("/country")
    @Operation(
        summary = "Obtener país de búsqueda actual",
        description = "Retorna la configuración actual del país utilizado por Nominatim para geocodificación"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Configuración obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = SystemConfig.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No se encontró configuración del sistema",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<SystemConfig> getCountry() {
        return ResponseEntity.ok(systemConfigService.getCountry());
    }

    @PatchMapping("/country/{countryCode}/{countryName}")
    @Operation(
        summary = "Actualizar país de búsqueda",
        description = "Cambia el país donde Nominatim buscará las ubicaciones. Usar código ISO 3166-1 alpha-2 (ej: cl, ar, pe)"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "País actualizado exitosamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = SystemConfig.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Código o nombre de país inválido",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No se encontró configuración del sistema",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<SystemConfig> updateCountry(
        @Parameter(
            description = "Código ISO 3166-1 alpha-2 del país",
            example = "cl",
            required = true
        )
        @PathVariable String countryCode,
        @Parameter(
            description = "Nombre del país",
            example = "Chile",
            required = true
        )
        @PathVariable String countryName) {
        return ResponseEntity.ok(systemConfigService.updateCountry(countryCode, countryName));
    }
}