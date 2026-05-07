package com.freemarket.reserva_service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Detalle de un producto dentro de la reserva")
public class ProductoReservaResponse {

    @Schema(description = "ID del producto", example = "5")
    public Long idProduct;

    @Schema(description = "Nombre del producto", example = "Auriculares Bluetooth")
    public String productName;

    @Schema(description = "Precio unitario del producto", example = "15990")
    public Integer unitPrice;

    @Schema(description = "Cantidad reservada", example = "3")
    public Integer quantity;

    @Schema(description = "Subtotal del item (precio x cantidad)", example = "47970")
    public Integer subtotal;
}