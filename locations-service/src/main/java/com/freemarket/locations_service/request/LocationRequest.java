package com.freemarket.locations_service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationRequest {

    public Long userId;
    public String address;
    public String comuna;
    public String region;

}
