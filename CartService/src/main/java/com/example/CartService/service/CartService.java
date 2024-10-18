package com.example.CartService.service;

import com.example.CartService.dto.CartRequest;
import com.example.CartService.dto.CartResponse;


public interface CartService {
    String createCartItem(CartRequest cartRequest);
    CartResponse getCartItems(Long userId);
    String removeItemFromCart(Long userId, int serviceId);
    String clearCart(Long userId);
}
