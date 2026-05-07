package com.freemarket.delivery_service.response;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta con los datos del delivery y su detalle")
public class DeliveryResponse {

    @Schema(description = "ID del delivery", example = "1")
    private Long idDelivery;

    @Schema(description = "Estado actual del delivery", example = "EN_CAMINO",
        allowableValues = {"PENDIENTE", "EN_CAMINO", "ENTREGADO", "CANCELADO"})
    private String status;

    @Schema(description = "ID de la reserva asociada al delivery", example = "10")
    private Long idReserva;

    @Schema(description = "Fecha de inicio del delivery", example = "2026-05-07")
    private LocalDate deliveryBeginDate;

    @Schema(description = "Fecha estimada de entrega", example = "2026-05-10")
    private LocalDate deliveryEndDate;
}