package com.example.pokerbackend.Exceptions;

public class ImageTooBigException extends RuntimeException {
    public ImageTooBigException(String message) {
        super(message);
    }
}
