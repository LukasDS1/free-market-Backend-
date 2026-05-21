package com.freemarket.privileges_service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
@AllArgsConstructor
public class AsignarPrivilegioRequest {
    private Long roleId;
    private Long privilegeId;
}