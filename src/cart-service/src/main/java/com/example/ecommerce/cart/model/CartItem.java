package com.example.ecommerce.cart.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.Instant;

public record CartItem(
        @NotBlank String productId,
        @NotBlank String productName,
        @Min(1) int quantity,
        @DecimalMin("0.0") BigDecimal unitPrice,
        Instant addedAt) {
}
