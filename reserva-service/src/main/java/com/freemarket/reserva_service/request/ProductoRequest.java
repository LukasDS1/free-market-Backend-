package com.freemarket.reserva_service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoRequest {

    public String proovedorNombre;
    
    public String name;

     public String url;

    public int price;

    public int stock;






}
