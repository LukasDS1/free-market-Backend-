package com.freemarket.reserva_service.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos requeridos para crear una nueva reserva")
public class ReserveRequest {
    @Schema(description = "ID del usuario que realiza la reserva", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    public Long idUser;
    
    @Schema(description = "Ubicacion del usuario", example = "Av.Siempre viva 3009", requiredMode = Schema.RequiredMode.REQUIRED)
    private String deliveryAddress;

    @Schema(description = "Lista de productos a reservar con sus cantidades", requiredMode = Schema.RequiredMode.REQUIRED)
    public List<ProductItemRequest> products;
}