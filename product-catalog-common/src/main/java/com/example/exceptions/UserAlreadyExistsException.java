package com.example.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String username) {
        super("Пользователь " + username + " уже существует");
    }
}