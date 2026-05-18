package com.freemarket.auth_service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRolRequest {
    
    public String rolName;
    public String rolDescription;
    

}
