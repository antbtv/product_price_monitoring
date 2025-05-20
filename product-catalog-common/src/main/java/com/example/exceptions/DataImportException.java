package com.example.exceptions;

public class DataImportException extends RuntimeException {
    public DataImportException(String message) {
        super(message);
    }
}