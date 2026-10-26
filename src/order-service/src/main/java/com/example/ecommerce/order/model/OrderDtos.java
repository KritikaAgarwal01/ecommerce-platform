package com.example.ecommerce.order.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public final class OrderDtos {
    private OrderDtos() {
    }

    public record CreateOrderRequest(
            @NotBlank String userId,
            @NotBlank String shippingAddress,
            @NotBlank String paymentMethod) {
    }

    public record UpdateOrderStatusRequest(@NotNull OrderStatus status) {
    }

    public record OrderResponse(
            UUID orderId,
            OrderStatus status,
            String message,
            BigDecimal totalAmount) {
    }
}
