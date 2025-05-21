package com.example.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("Пользователь с ID " + id + " не найден");
    }

    public UserNotFoundException(String username) {
        super("Пользователь " + username + " не найден");
    }
}