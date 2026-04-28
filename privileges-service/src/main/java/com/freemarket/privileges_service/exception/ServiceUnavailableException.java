package com.freemarket.privileges_service.exception;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String meesage){
        super(meesage);
    }

}
