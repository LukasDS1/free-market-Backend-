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
    private String email;
    private String password;    
    private String username;
    private String firstName;
    private String lastName;
    private String genre;


}
