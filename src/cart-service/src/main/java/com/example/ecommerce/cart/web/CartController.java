package com.example.ecommerce.cart.web;

import com.example.ecommerce.cart.model.AddCartItemRequest;
import com.example.ecommerce.cart.model.Cart;
import com.example.ecommerce.cart.model.UpdateQuantityRequest;
import com.example.ecommerce.cart.service.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    ResponseEntity<Cart> getCart(@PathVariable @NotBlank String userId) {
        return cartService.getCart(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}/items")
    ResponseEntity<Cart> addItem(
            @PathVariable @NotBlank String userId,
            @Valid @RequestBody AddCartItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addItem(userId, request));
    }

    @PutMapping("/{userId}/items/{productId}")
    ResponseEntity<Cart> updateItem(
            @PathVariable @NotBlank String userId,
            @PathVariable @NotBlank String productId,
            @Valid @RequestBody UpdateQuantityRequest request) {
        return cartService.updateQuantity(userId, productId, request.quantity())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{userId}")
    ResponseEntity<Void> clearCart(@PathVariable @NotBlank String userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
