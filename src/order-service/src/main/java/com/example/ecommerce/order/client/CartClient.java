package com.example.ecommerce.order.client;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class CartClient {
    private final RestClient client;

    public CartClient(
            RestClient.Builder builder,
            @Value("${services.cart.url:http://cart-service.ecommerce.svc.cluster.local}") String baseUrl) {
        this.client = builder.baseUrl(baseUrl).build();
    }

    public List<CartItem> getItems(String userId) {
        try {
            CartResponse response = client.get()
                    .uri("/api/cart/{userId}", userId)
                    .retrieve()
                    .body(CartResponse.class);
            return response == null || response.items() == null ? List.of() : response.items();
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().value() == 404) {
                return List.of();
            }
            throw exception;
        }
    }

    public void clear(String userId) {
        client.delete().uri("/api/cart/{userId}", userId).retrieve().toBodilessEntity();
    }

    public record CartItem(String productId, String productName, int quantity, BigDecimal unitPrice) {
    }

    private record CartResponse(String userId, List<CartItem> items) {
    }
}
