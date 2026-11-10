package com.example.dealership.error;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
