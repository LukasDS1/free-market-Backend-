package com.freemarket.delivery_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.freemarket.delivery_service.enums.DeliveryStatus;
import com.freemarket.delivery_service.model.Delivery;
import com.freemarket.delivery_service.response.DeliveryResponse;
import com.freemarket.delivery_service.service.DeliveryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api-v1/delivery")
@RequiredArgsConstructor
@Tag(name = "Delivery Controller", description = "Endpoints para gestión y seguimiento de deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping("/reserva/{idReserva}")
    @Operation(
        summary = "Obtener delivery por reserva",
        description = "Retorna el delivery y sus detalles asociados a una reserva específica"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Delivery encontrado exitosamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DeliveryResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No se encontró delivery para esa reserva",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<DeliveryResponse> getDelivery(
        @Parameter(description = "ID de la reserva a consultar", example = "10", required = true)
        @PathVariable Long idReserva) {
        return ResponseEntity.ok(deliveryService.getReserva(idReserva));
    }

    @PatchMapping("/reserva/{idReserva}/status")
    @Operation(
        summary = "Actualizar estado del delivery",
        description = "Actualiza el estado de un delivery asociado a una reserva"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Estado actualizado exitosamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DeliveryResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Delivery no encontrado",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Estado inválido",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<DeliveryResponse> updateStatus(
        @Parameter(description = "ID de la reserva", example = "10", required = true)
        @PathVariable Long idReserva,
        @Parameter(
            description = "Nuevo estado del delivery",
            example = "EN_CAMINO",
            schema = @Schema(allowableValues = {"PENDIENTE", "EN_CAMINO", "ENTREGADO", "CANCELADO"})
        )
        @RequestParam DeliveryStatus status) {
        return ResponseEntity.ok(deliveryService.updateStatus(idReserva, status));
    }

    @PatchMapping("/reserva/{idReserva}/cancel")
    @Operation(
        summary = "Cancelar delivery",
        description = "Cancela el delivery asociado a una reserva cambiando su estado a CANCELADO"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Delivery cancelado exitosamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Delivery no encontrado",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "409",
            description = "El delivery ya se encuentra cancelado o entregado",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<Void> cancelDelivery(
        @Parameter(description = "ID de la reserva a cancelar", example = "10", required = true)
        @PathVariable Long idReserva) {
        deliveryService.cancelDelivery(idReserva);
        return ResponseEntity.ok().build();
    }
}