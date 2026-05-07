package com.freemarket.auth_service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data


@Schema(description = "Respuesta de autenticación")
public class AuthResponse {

    @Schema(description = "JWT Token")
    private String token;

    @Schema(description = "Refresh Token")
    private String refreshToken;
}