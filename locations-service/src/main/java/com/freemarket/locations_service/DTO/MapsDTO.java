package com.freemarket.locations_service.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapsDTO {

   public double  latitude;
   public double  longitude;
   public String  formattedAddress;
   public String  comunaNombre;
   public String  regionNombre;

}
