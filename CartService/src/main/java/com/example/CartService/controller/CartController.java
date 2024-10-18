package com.example.CartService.controller;

import com.example.CartService.dto.CartRequest;
import com.example.CartService.dto.CartResponse;
import com.example.CartService.entity.Cart;
import com.example.CartService.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("carts")
public class CartController {

    @Autowired
    private  CartService cartService;

    @PostMapping
    public ResponseEntity<String> addTocart(@RequestBody CartRequest cartRequest){
        String response=cartService.createCartItem(cartRequest);
        return  new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<CartResponse> getItems(@RequestParam Long userId){
        CartResponse cartResponse=cartService.getCartItems(userId);
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }


    @DeleteMapping("/service")
    public ResponseEntity<String> removeCartItem(@RequestParam int serviceId, @RequestParam Long userId){
        String response=cartService.removeItemFromCart(userId, serviceId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<String> clearCart(@RequestParam Long userId){
       String cart= cartService.clearCart(userId);
       return  new ResponseEntity<>(cart, HttpStatus.OK);
    }

}
