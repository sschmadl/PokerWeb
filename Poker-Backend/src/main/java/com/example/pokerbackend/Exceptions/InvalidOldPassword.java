package com.example.pokerbackend.Exceptions;

public class InvalidOldPassword extends RuntimeException {
    public InvalidOldPassword(String message) {
        super(message);
    }
}
