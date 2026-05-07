package com.freemarket.auth_service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

@Schema(description = "Solicitud de login")
public class LoginRequest {

    @Schema(description = "Username del usuario", example = "luka123")
    private String username;

    @Schema(description = "Contraseña", example = "123456")
    private String password;
}