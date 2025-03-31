package com.example.pokerbackend.Exceptions;

public class InvalidImageFormat extends RuntimeException {
    public InvalidImageFormat(String message) {
        super(message);
    }
}
