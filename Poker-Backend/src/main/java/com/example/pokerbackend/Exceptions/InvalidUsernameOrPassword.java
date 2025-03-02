package com.example.pokerbackend.Exceptions;

public class InvalidUsernameOrPassword extends RuntimeException {
    public InvalidUsernameOrPassword(String message) {
        super(message);
    }
}
