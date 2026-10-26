package com.example.ecommerce.cart.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.ecommerce.cart.model.AddCartItemRequest;
import com.example.ecommerce.cart.model.Cart;
import com.example.ecommerce.cart.repository.CartRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    @Mock
    private CartRepository repository;

    @InjectMocks
    private CartService service;

    @Test
    void addsAnItemAndCalculatesTheTotal() {
        when(repository.findByUserId("user-1")).thenReturn(Optional.empty());
        when(repository.save(org.mockito.ArgumentMatchers.any(Cart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Cart cart = service.addItem("user-1",
                new AddCartItemRequest("product-1", "Product", 2, new BigDecimal("4.50")));

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getTotalPrice()).isEqualByComparingTo("9.00");
    }
}
