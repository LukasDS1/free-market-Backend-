package com.freemarket.reserva_service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Item de producto dentro de una reserva")
public class ProductItemRequest {

    @Schema(description = "ID del producto a reservar", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long idProduct;

    @Schema(description = "Cantidad del producto a reservar", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;
}