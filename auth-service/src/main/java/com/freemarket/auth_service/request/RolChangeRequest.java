package com.freemarket.auth_service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos para cambiar el rol de un usuario")
public class RolChangeRequest {
    @Schema(description = "ID del nuevo rol", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    public Long idRol;
    @Schema(description = "ID del usuario", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    public Long idUser;
}