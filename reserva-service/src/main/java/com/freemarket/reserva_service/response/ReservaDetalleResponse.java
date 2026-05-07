package com.freemarket.reserva_service.response;

import java.sql.Date;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta detallada de una reserva incluyendo sus productos")
public class ReservaDetalleResponse {

    @Schema(description = "ID de la reserva", example = "10")
    public Long idReserva;

    @Schema(description = "Fecha en que se realizó la reserva", example = "2026-05-07")
    public Date reserveDate;

    @Schema(description = "Precio total de la reserva en pesos", example = "47970")
    public Integer totalPrice;

    @Schema(description = "Estado actual de la reserva", example = "RESERVADO",
        allowableValues = {"RESERVADO", "PENDIENTE", "CANCELADO"})
    public String status;

    @Schema(description = "Lista de productos incluidos en la reserva")
    public List<ProductoReservaResponse> products;
}