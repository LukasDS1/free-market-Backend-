package com.freemarket.locations_service.excepcion;

public class ServiceUnavailableException extends RuntimeException {
     public ServiceUnavailableException(String meesage){
        super(meesage);
    }


}
