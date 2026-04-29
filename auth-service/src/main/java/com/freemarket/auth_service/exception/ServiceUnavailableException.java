package com.freemarket.auth_service.exception;

public class ServiceUnavailableException extends RuntimeException {
     public ServiceUnavailableException(String meesage){
        super(meesage);
    }

}
