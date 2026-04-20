package com.freemarket.locations_service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationBodegaResponse {

   public Long locationBodegaId;
   public String  streetAddress;
   public double  latitude;
   public double  longitude;
   public String  comunaNombre;
   public String  regionNombre;

}
