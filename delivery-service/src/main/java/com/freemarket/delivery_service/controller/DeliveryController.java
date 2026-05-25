package com.freemarket.delivery_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.freemarket.delivery_service.enums.DeliveryStatus;
import com.freemarket.delivery_service.request.ToDeliveryRequest;
import com.freemarket.delivery_service.response.DeliveryResponse;
import com.freemarket.delivery_service.service.DeliveryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api-v1/delivery")
@RequiredArgsConstructor
@Tag(name = "Delivery Controller", description = "Endpoints for delivery management and tracking")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping("/delivery/{idRepartidor}")
    @Operation(summary = "Get deliveries by courier", description = "Returns all deliveries associated to a specific courier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Deliveries retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeliveryResponse.class))),
        @ApiResponse(responseCode = "404", description = "No deliveries found for this courier",
            content = @Content(examples = @ExampleObject(value = "Delivery not found")))
    })
    public ResponseEntity<List<DeliveryResponse>> getDeliveryByRepartidor(@PathVariable Long idRepartidor) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByRepartidor(idRepartidor));
    }

    @PatchMapping("/take")
    @Operation(summary = "Take delivery", description = "Assigns a courier to a delivery and sets its status to EN_CAMINO")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Delivery taken successfully"),
        @ApiResponse(responseCode = "404", description = "Delivery or details not found",
            content = @Content(examples = @ExampleObject(value = "Delivery not found")))
    })
    public ResponseEntity<Void> tomarDelivery(
            @RequestBody ToDeliveryRequest deliveryRequest,
            @RequestHeader("X-User-Id") Long idRepartidor) {
        deliveryService.takeDelivery(deliveryRequest.getIdDeliveryDetails(), idRepartidor);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reserva/{idReserva}")
    @Operation(summary = "Get delivery by reservation", description = "Returns the delivery and its details associated to a specific reservation")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Delivery found successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeliveryResponse.class))),
        @ApiResponse(responseCode = "404", description = "Delivery not found for this reservation",
            content = @Content(examples = @ExampleObject(value = "Delivery not found")))
    })
    public ResponseEntity<DeliveryResponse> getDelivery(
            @Parameter(description = "Reservation ID", example = "10", required = true)
            @PathVariable Long idReserva) {
        return ResponseEntity.ok(deliveryService.getReserva(idReserva));
    }

    @PatchMapping("/reserva/status/{idReserva}")
    @Operation(summary = "Update delivery status", description = "Updates the status of a delivery associated to a reservation")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Status updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeliveryResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid status transition",
            content = @Content(examples = @ExampleObject(value = "cannot go back to PENDIENTE once the delivery is EN_CAMINO"))),
        @ApiResponse(responseCode = "404", description = "Delivery not found",
            content = @Content(examples = @ExampleObject(value = "Delivery not found"))),
        @ApiResponse(responseCode = "409", description = "Delivery has reached a final status",
            content = @Content(examples = @ExampleObject(value = "This delivery has reached a final status and cannot be changed")))
    })
    public ResponseEntity<DeliveryResponse> updateStatus(
            @Parameter(description = "Reservation ID", example = "10", required = true)
            @PathVariable Long idReserva,
            @Parameter(description = "New delivery status", example = "EN_CAMINO",
                schema = @Schema(allowableValues = {"PENDIENTE", "EN_CAMINO", "ENTREGADO", "CANCELADO"}))
            @RequestParam DeliveryStatus status) {
        return ResponseEntity.ok(deliveryService.updateStatus(idReserva, status));
    }

    @PatchMapping("/reserva/{idReserva}/cancel")
    @Operation(summary = "Cancel delivery", description = "Cancels the delivery associated to a reservation by setting its status to CANCELADO")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Delivery cancelled successfully"),
        @ApiResponse(responseCode = "404", description = "Delivery not found",
            content = @Content(examples = @ExampleObject(value = "Delivery not found"))),
        @ApiResponse(responseCode = "409", description = "Delivery is already cancelled or delivered",
            content = @Content(examples = @ExampleObject(value = "Delivery is already cancelled")))
    })
    public ResponseEntity<Void> cancelDelivery(
            @Parameter(description = "Reservation ID to cancel", example = "10", required = true)
            @PathVariable Long idReserva) {
        deliveryService.cancelDelivery(idReserva);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Get deliveries by user", description = "Returns all deliveries associated to a user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Deliveries retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeliveryResponse.class))),
        @ApiResponse(responseCode = "404", description = "No deliveries found for this user",
            content = @Content(examples = @ExampleObject(value = "Delivery not found")))
    })
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesByUsuario(
            @Parameter(description = "User ID", example = "42", required = true)
            @PathVariable Long idUsuario) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByUsuario(idUsuario));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get deliveries by status", description = "Returns all deliveries matching a given status")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Deliveries retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeliveryResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid status value",
            content = @Content(examples = @ExampleObject(value = "Invalid value 'UNKNOWN' for 'status'. Accepted values: PENDIENTE, EN_CAMINO, ENTREGADO, CANCELADO")))
    })
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesByStatus(
            @Parameter(description = "Status to filter by", example = "PENDIENTE", required = true)
            @PathVariable DeliveryStatus status) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByStatus(status));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all deliveries", description = "Returns all deliveries in the system")
    @ApiResponse(responseCode = "200", description = "Deliveries retrieved successfully")
    public ResponseEntity<List<DeliveryResponse>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }
}