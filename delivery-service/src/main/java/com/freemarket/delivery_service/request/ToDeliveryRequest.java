package com.freemarket.delivery_service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos requeridos para tomar un delivery")
public class ToDeliveryRequest {
    @Schema(description = "ID del detalle del delivery a tomar", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long idDeliveryDetails;
}