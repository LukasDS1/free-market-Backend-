package com.freemarket.reserva_service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos requeridos para crear o actualizar un producto")
public class ProductoRequest {
    @Schema(description = "Nombre del proveedor del producto", example = "Distribuidora Norte", requiredMode = Schema.RequiredMode.REQUIRED)
    public String proovedorNombre;

    @Schema(description = "Nombre del producto", example = "Auriculares Bluetooth", requiredMode = Schema.RequiredMode.REQUIRED)
    public String name;

    @Schema(description = "URL de la imagen del producto", example = "https://cdn.freemarket.com/productos/auriculares.png", requiredMode = Schema.RequiredMode.REQUIRED)
    public String url;

    @Schema(description = "Precio del producto en pesos", example = "15990", requiredMode = Schema.RequiredMode.REQUIRED)
    public Integer price;

    @Schema(description = "Stock disponible del producto", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    public Integer stock;
}