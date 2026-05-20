package com.freemarket.reserva_service.messaging.event;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservaCreatedEvent implements Serializable{
    private Long idReserva;
    private Long idUser;
    

}
