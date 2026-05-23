package com.freemarket.auth_service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Schema(description = "Respuesta con los datos del usuario")
public class UserResponse {
    @Schema(description = "ID del usuario", example = "1")
    public Long id;
    @Schema(description = "Estado del usuario", example = "ACTIVO")
    public String state;
    @Schema(description = "Nombre", example = "Lucas")
    public String firstname;
    @Schema(description = "Apellido", example = "Pérez")
    public String lastname;
    @Schema(description = "Email", example = "lucas@gmail.com")
    public String email;
    @Schema(description = "Nombre de usuario", example = "luka123")
    public String username;
    @Schema(description = "Nombre del rol", example = "ADMIN")
    public String rol;
    @Schema(description = "Género", example = "Masculino")
    public String genero;
    @Schema(description = "ID del rol", example = "2")
    public Long idRol;
}