package com.example.CartService.exception;

public class CartItemsNotPresent extends RuntimeException{
    public CartItemsNotPresent(String message) {
        super(message);
    }
}
