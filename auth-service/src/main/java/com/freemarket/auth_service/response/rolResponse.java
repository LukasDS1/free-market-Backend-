package com.freemarket.auth_service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class rolResponse {
    public String nombreRol;
    public Long idRol;
    public String descripcion;

}
