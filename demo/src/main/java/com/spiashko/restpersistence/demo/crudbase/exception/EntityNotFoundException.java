package com.spiashko.restpersistence.demo.crudbase.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String error) {
        super(error);
    }
}
