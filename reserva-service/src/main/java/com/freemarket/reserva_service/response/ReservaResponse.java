package com.freemarket.reserva_service.response;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservaResponse {

    public Long idReserva;
    public Long reserveDate;
    public int totalPrice;


    
    

}
