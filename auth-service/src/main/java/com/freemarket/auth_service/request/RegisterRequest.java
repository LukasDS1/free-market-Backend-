package com.freemarket.auth_service.request;

import com.freemarket.auth_service.model.Rol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class RegisterRequest {

    private String email;
    private String password;    
    private String username;
    private String firstName;
    private String lastName;
    private Rol rol; 


}
