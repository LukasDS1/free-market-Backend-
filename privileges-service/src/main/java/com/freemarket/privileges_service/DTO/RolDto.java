package com.freemarket.privileges_service.DTO;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO que representa un rol del sistema de autenticación")
public class RolDto {

    @Schema(description = "ID del rol", example = "2")
    private Long rolDtoId;

    @Schema(description = "Nombre del rol", example = "ROLE_ADMIN")
    private String rolDtoName;
}