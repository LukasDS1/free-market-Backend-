package com.freemarket.auth_service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta con los datos del rol")
public class rolResponse {
    @Schema(description = "Nombre del rol", example = "ADMIN")
    public String nombreRol;
    @Schema(description = "ID del rol", example = "2")
    public Long idRol;
    @Schema(description = "Descripción del rol", example = "Administrador del sistema")
    public String descripcion;
}