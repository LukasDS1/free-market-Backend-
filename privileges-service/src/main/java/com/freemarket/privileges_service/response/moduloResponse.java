package com.freemarket.privileges_service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta con los datos del módulo creado o consultado")
public class moduloResponse {

    @Schema(description = "ID del módulo", example = "1")
    public Long idModuloResponse;

    @Schema(description = "Nombre del módulo", example = "Inventario")
    public String ModuloName;
}