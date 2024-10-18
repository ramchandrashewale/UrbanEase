package com.example.CartService.repository;

import com.example.CartService.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<Object> findByCartIdAndServiceId(Long id, int serviceId);

    void deleteAllByCartId(Long id);

    Optional<CartItem> findByCart_IdAndServiceId(Long id, int serviceId);
}
