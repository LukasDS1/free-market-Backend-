package com.freemarket.config_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freemarket.config_service.request.ConfigRequest;
import com.freemarket.config_service.response.ConfigResponse;
import com.freemarket.config_service.service.configService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api-v1/config")
@RequiredArgsConstructor
@Tag(name = "Configuration Controller", description = "Endpoints para la gestión de configuración visual de comercios")
public class ConfigController {

    private final configService configService;

    @PostMapping("/create")
    @Operation(
        summary = "Crear configuración",
        description = "Crea la configuración visual inicial de un comercio"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Configuración creada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConfigResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de solicitud inválidos",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Ya existe una configuración para ese usuario o nombre de comercio",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<?> createConfig(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos para crear la configuración del comercio",
            required = true,
            content = @Content(schema = @Schema(implementation = ConfigRequest.class))
        )
        @RequestBody ConfigRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(configService.createConfiguration(request));
    }

    @PatchMapping("/update/{id}")
    @Operation(
        summary = "Actualizar configuración",
        description = "Actualiza parcialmente la configuración visual de un comercio por su ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Configuración actualizada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConfigResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Configuración no encontrada",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de solicitud inválidos",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<?> updateConfig(
        @Parameter(description = "ID de la configuración a actualizar", example = "1", required = true)
        @PathVariable Long id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos a actualizar de la configuración",
            required = true,
            content = @Content(schema = @Schema(implementation = ConfigRequest.class))
        )
        @RequestBody ConfigRequest request) {
        return ResponseEntity.ok().body(configService.updateConfiguration(id, request));
    }

    @GetMapping("/get/{id}")
    @Operation(
        summary = "Obtener configuración por usuario",
        description = "Retorna la configuración visual del comercio asociado a un usuario por su ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Configuración encontrada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConfigResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No se encontró configuración para ese usuario",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<ConfigResponse> getConfigurationByIdUser(
        @Parameter(description = "ID del usuario propietario del comercio", example = "42", required = true)
        @PathVariable Long id) {
        return ResponseEntity.ok().body(configService.getConfigurationByIdUser(id));
    }
}