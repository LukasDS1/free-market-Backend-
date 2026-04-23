package com.freemarket.reserva_service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ProductoResponse {


    public Long id;
    
    public String proovedorNombre;
    
    public String name;

    public int price;

    public int stock;


    

}
