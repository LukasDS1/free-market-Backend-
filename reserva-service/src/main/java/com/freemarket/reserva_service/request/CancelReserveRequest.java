package com.freemarket.reserva_service.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos requeridos para cancelar una reserva")
public class CancelReserveRequest {

    @Schema(description = "ID de la reserva a cancelar", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    public Long idReserve;

    @Schema(description = "ID del usuario que solicita la cancelación", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    public Long idUser;
}