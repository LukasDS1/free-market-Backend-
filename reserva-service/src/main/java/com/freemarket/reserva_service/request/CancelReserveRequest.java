package com.freemarket.reserva_service.request;
import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelReserveRequest {
    public Long idReserve;
    public Long idUser;    

}
