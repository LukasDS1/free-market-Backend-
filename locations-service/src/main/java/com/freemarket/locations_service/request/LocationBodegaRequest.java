package com.freemarket.locations_service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationBodegaRequest {

    public Long bodegaId;
    public String address;

}
