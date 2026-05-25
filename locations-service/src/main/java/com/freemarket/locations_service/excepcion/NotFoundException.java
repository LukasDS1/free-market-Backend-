package com.freemarket.locations_service.excepcion;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}