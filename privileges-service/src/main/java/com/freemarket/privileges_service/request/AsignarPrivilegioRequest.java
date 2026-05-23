package com.freemarket.privileges_service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos requeridos para asignar un privilegio a un rol")
public class AsignarPrivilegioRequest {
    @Schema(description = "ID del rol al que se asignará el privilegio", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long roleId;
    @Schema(description = "ID del privilegio a asignar", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long privilegeId;
}