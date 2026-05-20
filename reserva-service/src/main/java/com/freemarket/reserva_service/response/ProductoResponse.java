package com.freemarket.reserva_service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos de respuesta de un producto")
public class ProductoResponse {

    @Schema(description = "ID del producto", example = "5")
    public Long id;

    @Schema(description = "Nombre del proveedor", example = "Distribuidora Norte")
    public String proovedorNombre;

    @Schema(description = "Nombre del producto", example = "Auriculares Bluetooth")
    public String name;

    @Schema(description = "Imagen del producto", example = "imagen.png")
    public String url;

    @Schema(description = "Precio del producto en pesos", example = "15990")
    public Integer price;

    @Schema(description = "Stock disponible", example = "100")
    public Integer stock;
}