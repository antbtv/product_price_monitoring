package com.example.exceptions;

public class StoreNotFoundException extends RuntimeException {

    public StoreNotFoundException(Long id) {
        super("Магиназ с ID " + id + " не найден");
    }
}
