package com.freemarket.reserva_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freemarket.reserva_service.request.CancelReserveRequest;
import com.freemarket.reserva_service.request.ReserveRequest;
import com.freemarket.reserva_service.response.ReservaDetalleResponse;
import com.freemarket.reserva_service.response.ReservaResponse;
import com.freemarket.reserva_service.service.ReservaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api-v1/reserve")
@RequiredArgsConstructor
@Tag(
    name = "Reserva Controller",
    description = "Reserve management endpoints"
)
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping("/createReserve")
    @Operation(
        summary = "Create reserve",
        description = "Creates a new reserve with the selected products"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Reserve created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ReservaResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data or insufficient stock",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User or product not found",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<ReservaResponse> createReserve(

        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Reserve creation data",
            required = true,
            content = @Content(
                schema = @Schema(implementation = ReserveRequest.class)
            )
        )

        @RequestBody ReserveRequest request,

        @RequestHeader(
            value = "Idempotency-Key",
            defaultValue = ""
        )

        String idempotencyKey,

        @RequestHeader("X-User-Id")
        Long userId
    ) {

        request.setIdUser(userId);

        if (idempotencyKey.isBlank()) {
            idempotencyKey = UUID.randomUUID().toString();
        }

        return ResponseEntity.ok(
            reservaService.createReserva(request, idempotencyKey)
        );
    }

    @PatchMapping("/cancel")
    @Operation(
        summary = "Cancel reserve",
        description = "Cancels a reserve validating ownership"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Reserve cancelled successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Reserve not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "409",
            description = "User does not own this reserve or reserve is already cancelled",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<?> cancelReserva(

        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Reserve cancellation data",
            required = true,
            content = @Content(
                schema = @Schema(implementation = CancelReserveRequest.class)
            )
        )

        @RequestBody CancelReserveRequest request,

        @RequestHeader("X-User-Id")
        Long userId
    ) {

        request.setIdUser(userId);

        reservaService.deleteReserve(request);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{idUser}")
    @Operation(
        summary = "Get reserves by user",
        description = "Returns all reserves associated with a user"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Reserves retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ReservaDetalleResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<List<ReservaDetalleResponse>> getReservasByUser(

        @Parameter(
            description = "User id",
            example = "42",
            required = true
        )

        @PathVariable Long idUser
    ) {

        return ResponseEntity.ok(
            reservaService.getReservasByUser(idUser)
        );
    }

    @GetMapping("/getallreserve")
    @Operation(
        summary = "Get all reserves",
        description = "Returns all registered reserves"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Reserves retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ReservaResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Reserves not found",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<List<ReservaResponse>> getAllreservas() {

        return ResponseEntity.ok(
            reservaService.getAllReservas()
        );
    }

    @GetMapping("/{idReserva}")
    @Operation(
        summary = "Get reserve by id",
        description = "Returns a reserve by its id"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Reserve retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ReservaDetalleResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Reserve not found",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<ReservaDetalleResponse> getReservaById(

        @PathVariable Long idReserva
    ) {

        return ResponseEntity.ok(
            reservaService.getReservaById(idReserva)
        );
    }

    @PatchMapping("/cancel/{idReserva}")
    @Operation(
        summary = "Cancel reserve by admin",
        description = "Cancels a reserve by id"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Reserve cancelled successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Reserve not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Reserve is already cancelled",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<?> cancelReservaAdmin(

        @PathVariable Long idReserva
    ) {

        reservaService.cancelReservaAdmin(idReserva);

        return ResponseEntity.ok().build();
    }
}