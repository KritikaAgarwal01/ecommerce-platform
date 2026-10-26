package com.example.ecommerce.cart.service;

import com.example.ecommerce.cart.model.AddCartItemRequest;
import com.example.ecommerce.cart.model.Cart;
import com.example.ecommerce.cart.model.CartItem;
import com.example.ecommerce.cart.repository.CartRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    private final CartRepository repository;

    public CartService(CartRepository repository) {
        this.repository = repository;
    }

    public Optional<Cart> getCart(String userId) {
        return repository.findByUserId(userId);
    }

    public Cart addItem(String userId, AddCartItemRequest request) {
        Cart cart = repository.findByUserId(userId).orElseGet(() -> newCart(userId));
        var items = new ArrayList<>(cart.getItems());
        boolean updated = false;
        for (int index = 0; index < items.size(); index++) {
            CartItem current = items.get(index);
            if (current.productId().equals(request.productId())) {
                items.set(index, new CartItem(current.productId(), current.productName(),
                        current.quantity() + request.quantity(), current.unitPrice(), current.addedAt()));
                updated = true;
                break;
            }
        }
        if (!updated) {
            items.add(new CartItem(request.productId(), request.productName(), request.quantity(),
                    request.unitPrice(), Instant.now()));
        }
        updateTotals(cart, items);
        return repository.save(cart);
    }

    public Optional<Cart> updateQuantity(String userId, String productId, int quantity) {
        Optional<Cart> existing = repository.findByUserId(userId);
        if (existing.isEmpty()) {
            return Optional.empty();
        }
        Cart cart = existing.get();
        var items = new ArrayList<>(cart.getItems());
        boolean found = items.removeIf(item -> item.productId().equals(productId) && quantity == 0);
        if (quantity > 0) {
            for (int index = 0; index < items.size(); index++) {
                CartItem current = items.get(index);
                if (current.productId().equals(productId)) {
                    items.set(index, new CartItem(current.productId(), current.productName(),
                            quantity, current.unitPrice(), current.addedAt()));
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            return Optional.empty();
        }
        updateTotals(cart, items);
        return Optional.of(repository.save(cart));
    }

    public void clearCart(String userId) {
        repository.deleteByUserId(userId);
    }

    private Cart newCart(String userId) {
        Cart cart = new Cart();
        cart.setId(userId);
        cart.setUserId(userId);
        return cart;
    }

    private void updateTotals(Cart cart, ArrayList<CartItem> items) {
        cart.setItems(items);
        cart.setTotalPrice(items.stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        cart.setUpdatedAt(Instant.now());
    }
}
