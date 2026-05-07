package com.freemarket.auth_service.request;

import com.freemarket.auth_service.model.Rol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

@Schema(description = "Solicitud de registro")
public class RegisterRequest {

    @Schema(description ="Email del usuario",example = "admin@gmail.com")
    private String email;

    @Schema(description = "Contraseña del usuario",example = "123456")
    private String password;

    @Schema(description = "Nombre de usuario",example = "admin123")
    private String username;

    @Schema(description = "Nombre de pila del usuario",example = "admin")
    private String firstName;

    @Schema(description = "Apellido del usuario",example = "adminJr")
    private String lastName;

    @Schema(description = "Genero del usuario",example = "Masculino")
    private String genre;

    @Schema(description = "Rol del usuario",example = "USER")
    private Rol rol;
}