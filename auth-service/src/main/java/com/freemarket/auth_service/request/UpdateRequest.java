package com.freemarket.auth_service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class UpdateRequest {
    private Long userId;
    private String email;
    private String password;    
    private String username;
    private String firstName;
    private String lastName;


}
