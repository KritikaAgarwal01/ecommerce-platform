package com.example.ecommerce.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.ecommerce.payment.model.Payment;
import com.example.ecommerce.payment.model.PaymentRequest;
import com.example.ecommerce.payment.model.PaymentStatus;
import com.example.ecommerce.payment.repository.PaymentRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentProcessorTest {
    @Mock
    private PaymentRepository repository;

    @InjectMocks
    private PaymentProcessor processor;

    @Test
    void processesPaymentSuccessfully() {
        when(repository.save(org.mockito.ArgumentMatchers.any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = processor.process(new PaymentRequest(
                UUID.randomUUID(), new BigDecimal("25.00"), "USD", "card"));

        assertThat(response.status()).isEqualTo(PaymentStatus.Succeeded);
        assertThat(response.gatewayTransactionId()).startsWith("txn_");
    }
}
