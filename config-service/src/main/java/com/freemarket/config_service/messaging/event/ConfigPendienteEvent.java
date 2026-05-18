package com.freemarket.config_service.messaging.event;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigPendienteEvent implements Serializable{

    public enum OperationType{CREATE,UPDATE}

    private Long          idUser;
    private String        commerceName;
    private String        logoUrl;
    private String        favicomUrl;
    private String        primaryColor;
    private String        secondaryColor;
    private String        principalFont;
    private OperationType operationType;
}
