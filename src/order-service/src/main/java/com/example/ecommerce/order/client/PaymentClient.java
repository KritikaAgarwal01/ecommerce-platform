package com.example.ecommerce.order.client;

import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PaymentClient {
    private final RestClient client;

    public PaymentClient(
            RestClient.Builder builder,
            @Value("${services.payment.url:http://payment-service.ecommerce.svc.cluster.local}") String baseUrl) {
        this.client = builder.baseUrl(baseUrl).build();
    }

    public PaymentResult process(UUID orderId, BigDecimal amount, String paymentMethod) {
        PaymentResponse response = client.post()
                .uri("/api/payments")
                .body(new PaymentRequest(orderId, amount, "USD", paymentMethod))
                .retrieve()
                .body(PaymentResponse.class);
        return response == null
                ? new PaymentResult(false, null)
                : new PaymentResult("Succeeded".equals(response.status()), response.paymentId());
    }

    public void refund(UUID paymentId) {
        client.post().uri("/api/payments/{id}/refund", paymentId).retrieve().toBodilessEntity();
    }

    private record PaymentRequest(UUID orderId, BigDecimal amount, String currency, String paymentMethod) {
    }

    private record PaymentResponse(UUID paymentId, String status) {
    }

    public record PaymentResult(boolean success, UUID paymentId) {
    }
}
