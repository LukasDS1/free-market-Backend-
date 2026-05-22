package com.freemarket.auth_service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserResponse {

    public Long id;
    public String state;
    public String firstname;
    public String lastname;
    public String email;
    public String username;
    public String rol;
    public String genero;
    public Long idRol; 

}
