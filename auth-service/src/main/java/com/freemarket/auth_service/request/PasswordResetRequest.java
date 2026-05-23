package com.freemarket.auth_service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud de recuperación de contraseña")
public class PasswordResetRequest {
    @Schema(description = "Email del usuario", example = "lucas@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
}