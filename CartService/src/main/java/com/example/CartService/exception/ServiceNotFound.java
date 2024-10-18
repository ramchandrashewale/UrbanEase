package com.example.CartService.exception;

public class ServiceNotFound extends RuntimeException {
    private String message;
    public ServiceNotFound(String message) {
        super(message);
    }
}
