package com.example.ecommerce.cart.repository;

import com.example.ecommerce.cart.model.Cart;
import java.util.Optional;

public interface CartRepository {
    Optional<Cart> findByUserId(String userId);

    Cart save(Cart cart);

    void deleteByUserId(String userId);
}
