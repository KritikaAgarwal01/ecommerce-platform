package com.example.ecommerce.payment.service;

import com.example.ecommerce.payment.model.Payment;
import com.example.ecommerce.payment.model.PaymentRequest;
import com.example.ecommerce.payment.model.PaymentResponse;
import com.example.ecommerce.payment.model.PaymentStatus;
import com.example.ecommerce.payment.repository.PaymentRepository;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentProcessor.class);
    private final PaymentRepository repository;

    public PaymentProcessor(PaymentRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PaymentResponse process(PaymentRequest request) {
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setOrderId(request.orderId());
        payment.setAmount(request.amount());
        payment.setCurrency(request.currency() == null || request.currency().isBlank() ? "USD" : request.currency());
        payment.setPaymentMethod(request.paymentMethod());
        payment.setStatus(PaymentStatus.Processing);
        payment.setCreatedAt(Instant.now());

        payment.setStatus(PaymentStatus.Succeeded);
        payment.setGatewayTransactionId("txn_" + UUID.randomUUID().toString().replace("-", ""));
        payment.setCompletedAt(Instant.now());
        repository.save(payment);

        LOGGER.info("Payment {} for order {} completed with status {}",
                payment.getId(), payment.getOrderId(), payment.getStatus());
        return response(payment, "Payment processed successfully");
    }

    @Transactional
    public PaymentResponse refund(UUID paymentId) {
        return repository.findById(paymentId)
                .map(payment -> {
                    payment.setStatus(PaymentStatus.Refunded);
                    payment.setCompletedAt(Instant.now());
                    repository.save(payment);
                    LOGGER.info("Payment {} refunded", paymentId);
                    return response(payment, "Refund processed successfully");
                })
                .orElseGet(() -> new PaymentResponse(
                        paymentId, PaymentStatus.Failed, null, "Payment not found"));
    }

    private PaymentResponse response(Payment payment, String message) {
        return new PaymentResponse(payment.getId(), payment.getStatus(),
                payment.getGatewayTransactionId(), message);
    }
}
