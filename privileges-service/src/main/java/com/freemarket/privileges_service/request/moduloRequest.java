package com.freemarket.privileges_service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos requeridos para crear un nuevo módulo del sistema")
public class moduloRequest {

    @Schema(description = "Nombre del módulo a crear", example = "Inventario", requiredMode = Schema.RequiredMode.REQUIRED)
    public String moduloname;
}