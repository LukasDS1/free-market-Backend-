package com.freemarket.auth_service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos requeridos para crear un nuevo rol")
public class CreateRolRequest {
    @Schema(description = "Nombre del rol", example = "VENDEDOR", requiredMode = Schema.RequiredMode.REQUIRED)
    public String rolName;
    @Schema(description = "Descripción del rol", example = "Puede gestionar productos y reservas", requiredMode = Schema.RequiredMode.REQUIRED)
    public String rolDescription;
}   