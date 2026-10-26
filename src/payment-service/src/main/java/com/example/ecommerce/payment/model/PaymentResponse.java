package com.example.ecommerce.payment.model;

import java.util.UUID;

public record PaymentResponse(
        UUID paymentId,
        PaymentStatus status,
        String gatewayTransactionId,
        String message) {
}
