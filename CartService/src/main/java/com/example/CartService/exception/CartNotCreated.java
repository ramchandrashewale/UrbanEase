package com.example.CartService.exception;

public class CartNotCreated extends RuntimeException{
    private String msg;
    public CartNotCreated(String msg){
        super(msg);
    }
}
