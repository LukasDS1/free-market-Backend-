package com.freemarket.auth_service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor

@Schema(description = "Solicitud para actualizar usuario")
public class UpdateRequest {
    @Schema(description = "Email del usuario", example = "nuevo@gmail.com")
    private String email;
    @Schema(description = "Contraseña", example = "nuevaPass123")
    private String password;
    @Schema(description = "Nombre de usuario", example = "luka123")
    private String username;
    @Schema(description = "Nombre", example = "Lucas")
    private String firstName;
    @Schema(description = "Apellido", example = "Pérez")
    private String lastName;
    @Schema(description = "Género", example = "Masculino")
    private String genre;
}