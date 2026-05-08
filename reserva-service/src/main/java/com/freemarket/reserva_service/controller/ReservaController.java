package com.freemarket.reserva_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.freemarket.reserva_service.request.CancelReserveRequest;
import com.freemarket.reserva_service.request.ReserveRequest;
import com.freemarket.reserva_service.response.ReservaDetalleResponse;
import com.freemarket.reserva_service.response.ReservaResponse;
import com.freemarket.reserva_service.service.ReservaService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping("api-v1/reserve")
@RequiredArgsConstructor
@Tag(name = "Reserva Controller", description = "Endpoints para gestión de reservas de productos")
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping("/createReserve")
    @Operation(summary = "Crear reserva", description = "Crea una nueva reserva con los productos e cantidades indicados para un usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reserva creada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos o stock insuficiente",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Usuario o producto no encontrado",
            content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ReservaResponse> createReserve(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos para crear la reserva incluyendo usuario y lista de productos",
            required = true,
            content = @Content(schema = @Schema(implementation = ReserveRequest.class)))
        @RequestBody ReserveRequest request ,@RequestHeader(value = "Idempotency-Key", defaultValue = "") String idempotencyKey) {

        if (idempotencyKey.isBlank()) {
        idempotencyKey = UUID.randomUUID().toString();
    }
        return ResponseEntity.ok().body(reservaService.createReserva(request,idempotencyKey));
    }

    @PatchMapping("/cancel")
    @Operation(summary = "Cancelar reserva", description = "Cancela una reserva activa validando que el usuario sea el propietario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reserva cancelada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos para cancelar esta reserva",
            content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<?> cancelReserva(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos para cancelar la reserva",
            required = true,
            content = @Content(schema = @Schema(implementation = CancelReserveRequest.class)))
        @RequestBody CancelReserveRequest request) {
        reservaService.deleteReserve(request);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/user/{idUser}")
@Operation(
    summary = "Obtener reservas por usuario",
    description = "Retorna todas las reservas asociadas a un usuario con el detalle de sus productos"
)
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Reservas obtenidas exitosamente",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ReservaDetalleResponse.class))
    ),
    @ApiResponse(
        responseCode = "404",
        description = "Usuario no encontrado",
        content = @Content(mediaType = "application/json")
    ),
    @ApiResponse(
        responseCode = "200",
        description = "Lista vacía si el usuario no tiene reservas",
        content = @Content(mediaType = "application/json")
    )
})
public ResponseEntity<List<ReservaDetalleResponse>> getReservasByUser(
    @Parameter(description = "ID del usuario a consultar", example = "42", required = true)
    @PathVariable Long idUser) {
    return ResponseEntity.ok().body(reservaService.getReservasByUser(idUser));
}
}