package com.freemarket.auth_service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolChangeRequest {
    
    public Long idRol;
    public Long idUser;

}
