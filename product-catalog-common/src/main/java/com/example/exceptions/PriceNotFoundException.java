package com.example.exceptions;

public class PriceNotFoundException extends RuntimeException {
    public PriceNotFoundException(Long id) {
        super("Цена с ID " + id + " не найдена");
    }
}