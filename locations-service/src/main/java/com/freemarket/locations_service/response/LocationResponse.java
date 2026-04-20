package com.freemarket.locations_service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponse {
   public Long locationId;
   public Long userId;
   public String  streetAddress;
   public double  latitude;
   public double  longitude;
   public String  comunaNombre;
   public String  regionNombre;

}
