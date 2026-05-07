package com.freemarket.privileges_service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta con el privilegio y el módulo al que pertenece")
public class ResponseDTO {

    @Schema(description = "Nombre del privilegio", example = "CREAR_PRODUCTO")
    private String privilegeName;

    @Schema(description = "Nombre del módulo al que pertenece el privilegio", example = "Inventario")
    private String moduloName;
}