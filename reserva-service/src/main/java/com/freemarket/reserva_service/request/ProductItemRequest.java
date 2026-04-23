package com.freemarket.reserva_service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductItemRequest {

    private Long idProduct;
    private int quantity;

}
