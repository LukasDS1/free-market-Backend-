package com.freemarket.auth_service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para cambiar la contraseña con token")
public class PasswordChangeRequest {
    @Schema(description = "Token recibido por email", example = "abc123token", requiredMode = Schema.RequiredMode.REQUIRED)
    private String token;
    @Schema(description = "Nueva contraseña", example = "nuevaPass123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
}