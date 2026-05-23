package com.freemarket.privileges_service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos requeridos para crear un nuevo privilegio")
public class PrivilegeRequest {
    @Schema(description = "Nombre del privilegio a crear", example = "CREAR_PRODUCTO", requiredMode = Schema.RequiredMode.REQUIRED)
    private String privilegeName;
    @Schema(description = "ID del módulo al que pertenece el privilegio", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long moduloId;
}