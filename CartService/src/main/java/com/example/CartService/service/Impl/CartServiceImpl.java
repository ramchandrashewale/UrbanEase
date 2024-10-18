package com.example.CartService.service.Impl;

import com.example.CartService.dto.CartRequest;
import com.example.CartService.dto.CartResponse;
import com.example.CartService.dto.ServiceResult;
import com.example.CartService.entity.Cart;
import com.example.CartService.entity.CartItem;
import com.example.CartService.entity.ServiceResponse;
import com.example.CartService.exception.CartException;
import com.example.CartService.exception.CartItemsNotPresent;
import com.example.CartService.exception.CartNotCreated;
import com.example.CartService.exception.ServiceNotFound;
import com.example.CartService.repository.CartItemRepository;
import com.example.CartService.repository.CartRepository;
import com.example.CartService.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private WebClient webClient;

    /**Use to store post service in cart
     * @param cartRequest contains service to add in cart
     * @return success  message
     */

    @Override
    public String createCartItem(CartRequest cartRequest) {
            try {
                // Fetch the existing cart or create a new one if not present
                Cart cart = cartRepository.findByUserId(cartRequest.getUserId())
                        .orElseGet(() -> createNewCart(cartRequest.getUserId()));

                ServiceResponse serviceResponse = serviceResponse(cartRequest.getServiceId());
                // Create a new CartItem from the CartRequest
                updateCartItemQuantity(cartRequest, serviceResponse, cart);

                return "Item added to Cart";
            } catch (WebClientException exception) {
                throw exception;
            }catch (Exception exception) {
                throw new CartException("Database Access error");
            }
    }

    /**
     * Method to create new cart
     * @param userId used as to connect user database
     * @return new created cart
     */
    private Cart createNewCart(Long userId) {
        Cart newCart = Cart.builder()
                .userId(userId)
                .cartItems(new ArrayList<>()) // Initialize with an empty list
                .build();
        return cartRepository.save(newCart); // Save and return the new cart
    }

    /**
     * Method is used to store cartItems
     * @param cartRequest to retrieve serviceId
     * @param serviceResponse to store in cartItem
     * @param cart to add the cartId
     */
    private void updateCartItemQuantity(CartRequest cartRequest, ServiceResponse serviceResponse, Cart cart) {
        CartItem newItem = CartItem.builder()
                .serviceId(cartRequest.getServiceId())
                .description(serviceResponse.getDescription())
                .serviceName(serviceResponse.getName())
                .quantity(cartRequest.getQuantity())
                .price(serviceResponse.getPrice())
                .cart(cart) // Associate with the cart
                .build();
        cartItemRepository.save(newItem);
    }

    /**
     * Used to find service from service microservice
     * @param id passes service id
     * @return service response
     */
    private ServiceResponse serviceResponse(int id) {
       return webClient.get().uri(uriBuilder -> uriBuilder
                .path("/singleService")
                .queryParam("id", id)
                .build())
                .retrieve()
                .bodyToMono(ServiceResponse.class)
                .block();
    }

    /**To return list of cartItems
     *Associate to user
     * @param userId to retrive cartItems
     * @return List of service with total sum
     */
    @Override
    public CartResponse getCartItems(Long userId) {
        try {
            Optional<Cart> cart = cartRepository.findByUserId(userId);

            // Retrieve cart items
            List<CartItem> cartItems = cart.get().getCartItems();
            if (cartItems == null || cartItems.isEmpty()) {
                throw new CartItemsNotPresent("Cart is empty for user ID: " + userId);
            }

            // Prepare the service list and calculate total price
            return carteResponse(cartItems);
        } catch (CartItemsNotPresent exception) {
            throw exception;
        } catch (Exception exception) {
            throw new CartException("Database Access error");
        }
    }

    /**
     * Internal method to create CartResponse
     * @param cartItems return from database
     * @return list of service and total price
     */
    private CartResponse carteResponse(List<CartItem> cartItems) {
        // Calculate the total price using a stream
        double totalPrice = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        // Convert CartItems to ServiceResults using a stream
        List<ServiceResult> services = cartItems.stream()
                .map(item -> ServiceResult.builder()
                        .serviceDescription(item.getDescription())
                        .serviceName(item.getServiceName())
                        .servicePrice(item.getPrice())
                        .serviceQuantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        // Build and return the CartResponse
        return CartResponse.builder()
                .serviceResponses(services)
                .totalPrice(totalPrice)
                .build();
    }

    /**
     * Update quantity
     * @param userId to find cart
     * @param serviceId to find service
     * @param quantity to update quantity of service
     * @return Success message
     */
    public String updateCartItemQuantity(Long userId, int serviceId, Integer quantity) {
        try {
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new CartNotCreated("Cart not created for user " + userId));

            CartItem item = (CartItem) cartItemRepository.findByCartIdAndServiceId(cart.getId(), serviceId)
                    .orElseThrow(() -> new CartItemsNotPresent("Item not found in cart"));

            // Update the item quantity
            item.setQuantity(quantity);
            cartItemRepository.save(item);
            return "Updated Quantity";
        } catch (CartNotCreated | CartItemsNotPresent exception) {
            throw exception;
        } catch (Exception exception) {
            throw new CartException("Database Access error");
        }
    }

    /**
     * Used to remove cart from the system
     * @param userId to find cart
     * @param serviceId to find service in cartItems
     * @return message after completing task
     */
    public String removeItemFromCart(Long userId, int serviceId) {
        try {
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new CartNotCreated("Cart not found for user " + userId));

            // Find the specific cart item for the serviceId and cartId
            CartItem cartItem = cartItemRepository.findByCart_IdAndServiceId(cart.getId(), serviceId)
                    .orElseThrow(() -> new ServiceNotFound("Service not found in cart"));

            // Delete the specific cart item
            cartItemRepository.delete(cartItem);
            return "Service removed";
        } catch (CartNotCreated | ServiceNotFound exception) {
            throw exception;
        } catch (Exception e) {
            throw new CartException("Database Access error");
        }
    }

    /**
     *  Remove all items inside the cart
     * @param userId to find cart
     * @return success message
     */
    public String clearCart(Long userId) {
        try {
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new CartNotCreated("Cart not found for user " + userId));

            // Delete all items from the cart
            cartItemRepository.deleteAllByCartId(cart.getId());
            return "ALL items were remove add something to procced";
        } catch (CartNotCreated exception) {
            throw exception;
        } catch (Exception e) {
            throw new CartException("Database Access error");
        }
    }


}
