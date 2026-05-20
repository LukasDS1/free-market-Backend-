package com.freemarket.locations_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freemarket.locations_service.request.LocationRequest;
import com.freemarket.locations_service.response.LocationResponse;
import com.freemarket.locations_service.response.LocationResponseForId;
import com.freemarket.locations_service.service.LocationsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api-v1/location")
@AllArgsConstructor
@Tag(name = "Location Controller", description = "Endpoints para gestión de ubicaciones de usuarios")
public class LocationController {

    private final LocationsService locationsService;

    @PostMapping("/createLocation")
    @Operation(
        summary = "Crear ubicación",
        description = "Registra una nueva ubicación para un usuario geocodificando la dirección ingresada mediante el servicio de mapas"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Ubicación creada exitosamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = LocationResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dirección inválida o no encontrada por el servicio de mapas",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrado",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<LocationResponse> createLocation(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de la ubicación a registrar",
            required = true,
            content = @Content(schema = @Schema(implementation = LocationRequest.class))
        )
        @RequestBody LocationRequest request , @RequestHeader ("X-User-Id") Long userId) {
        request.setUserId(userId);
        return ResponseEntity.ok().body(locationsService.createUserLocation(request));
    }

    @PutMapping("/updateLocation")
    @Operation(
        summary = "Actualizar ubicación",
        description = "Actualiza la ubicación de un usuario geocodificando la nueva dirección ingresada"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Ubicación actualizada exitosamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = LocationResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dirección inválida o no encontrada por el servicio de mapas",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Ubicación o usuario no encontrado",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<LocationResponse> updateLocation(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de la nueva ubicación a registrar",
            required = true,
            content = @Content(schema = @Schema(implementation = LocationRequest.class))
        )
        @RequestBody LocationRequest request ,@RequestHeader("X-User-Id") Long userId) {
        request.setUserId(userId);
        return ResponseEntity.ok(locationsService.updateLocation(request));
    }


    @GetMapping("/getLocation/{id}")
    @Operation(
    summary = "Obtener ubicación por ID de usuario",
    description = "Retorna la ubicación registrada de un usuario, incluyendo su dirección, comuna y región"
    )
    @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Ubicación encontrada exitosamente",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = LocationResponseForId.class))
    ),
    @ApiResponse(
        responseCode = "404",
        description = "Usuario o ubicación no encontrada",
        content = @Content(mediaType = "application/json")
    )
    })
    public ResponseEntity<LocationResponseForId> getLocationByUserId(
    @Parameter(
        description = "ID del usuario cuya ubicación se desea obtener",
        required = true,
        example = "1"
    )
    @PathVariable Long id) {
    return ResponseEntity.ok(locationsService.getLocationByUserId(id));
}
}
