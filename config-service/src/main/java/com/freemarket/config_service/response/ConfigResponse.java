package com.freemarket.config_service.response;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConfigResponse {
    public String commerceName;
    public String logoUrl;
    public String favicomUrl;
    public String principalFont;
    public String primaryColor;
    public String secondaryColor; 
    public Date updateDate;


}
