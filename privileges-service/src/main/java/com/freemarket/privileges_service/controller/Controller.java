package com.freemarket.privileges_service.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.freemarket.privileges_service.request.moduloRequest;
import com.freemarket.privileges_service.response.ResponseDTO;
import com.freemarket.privileges_service.response.moduloResponse;
import com.freemarket.privileges_service.service.Services;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("api-v1/privileges")
@AllArgsConstructor
@Tag(name = "Privileges Controller", description = "Endpoints para gestión de módulos y privilegios por rol")
public class Controller {

    private final Services services;

    @PostMapping("/modules")
    @Operation(
        summary = "Crear módulo",
        description = "Crea un nuevo módulo del sistema al que se podrán asociar privilegios"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Módulo creado exitosamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = moduloResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de solicitud inválidos",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Ya existe un módulo con ese nombre",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<moduloResponse> createModulo(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos para crear el módulo",
            required = true,
            content = @Content(schema = @Schema(implementation = moduloRequest.class))
        )
        @RequestBody moduloRequest request) {
        moduloResponse response = services.createModulo(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/role/{id}")
    @Operation(
        summary = "Obtener privilegios por rol",
        description = "Retorna la lista de privilegios y sus módulos asociados para un rol específico"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Privilegios obtenidos exitosamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No se encontraron privilegios para el rol indicado",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<List<ResponseDTO>> getPrivilegesByRole(
        @Parameter(description = "ID del rol a consultar", example = "2", required = true)
        @PathVariable Long id) {
        List<ResponseDTO> response = services.getPrivilegesByRole(id);
        return ResponseEntity.ok(response);
    }
}
