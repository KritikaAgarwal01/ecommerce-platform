package com.example.ecommerce.order.service;

import com.example.ecommerce.order.client.CartClient;
import com.example.ecommerce.order.client.PaymentClient;
import com.example.ecommerce.order.model.Order;
import com.example.ecommerce.order.model.OrderDtos.CreateOrderRequest;
import com.example.ecommerce.order.model.OrderDtos.OrderResponse;
import com.example.ecommerce.order.model.OrderItem;
import com.example.ecommerce.order.model.OrderStatus;
import com.example.ecommerce.order.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderFulfillmentService {
    private final OrderRepository repository;
    private final PaymentClient paymentClient;
    private final CartClient cartClient;

    public OrderFulfillmentService(
            OrderRepository repository,
            PaymentClient paymentClient,
            CartClient cartClient) {
        this.repository = repository;
        this.paymentClient = paymentClient;
        this.cartClient = cartClient;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        var cartItems = cartClient.getItems(request.userId());
        if (cartItems.isEmpty()) {
            return new OrderResponse(null, OrderStatus.Cancelled, "Cart is empty", BigDecimal.ZERO);
        }

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setUserId(request.userId());
        order.setShippingAddress(request.shippingAddress());
        order.setStatus(OrderStatus.Pending);
        order.setCreatedAt(Instant.now());
        order.setItems(cartItems.stream().map(item -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setId(UUID.randomUUID());
            orderItem.setOrder(order);
            orderItem.setProductId(item.productId());
            orderItem.setProductName(item.productName());
            orderItem.setQuantity(item.quantity());
            orderItem.setUnitPrice(item.unitPrice());
            return orderItem;
        }).toList());
        order.setTotalAmount(order.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        repository.save(order);

        order.setStatus(OrderStatus.PaymentProcessing);
        var payment = paymentClient.process(order.getId(), order.getTotalAmount(), request.paymentMethod());
        if (payment.success()) {
            order.setStatus(OrderStatus.Confirmed);
            order.setPaymentId(payment.paymentId());
            cartClient.clear(request.userId());
        } else {
            order.setStatus(OrderStatus.Cancelled);
        }
        order.setUpdatedAt(Instant.now());
        repository.save(order);
        String message = payment.success()
                ? "Order confirmed and payment processed"
                : "Order creation failed - payment was not successful";
        return response(order, message);
    }

    @Transactional
    public OrderResponse updateStatus(UUID orderId, OrderStatus status) {
        Order order = repository.findById(orderId).orElse(null);
        if (order == null) {
            return null;
        }
        order.setStatus(status);
        order.setUpdatedAt(Instant.now());
        if (status == OrderStatus.Completed) {
            order.setCompletedAt(Instant.now());
        }
        repository.save(order);
        return response(order, "Order status updated to " + status);
    }

    @Transactional
    public OrderResponse cancel(UUID orderId) {
        Order order = repository.findById(orderId).orElse(null);
        if (order == null) {
            return null;
        }
        if (order.getStatus() == OrderStatus.Completed || order.getStatus() == OrderStatus.Delivered) {
            return response(order, "Cannot cancel a completed or delivered order");
        }
        order.setStatus(OrderStatus.Cancelled);
        order.setUpdatedAt(Instant.now());
        repository.save(order);
        if (order.getPaymentId() != null) {
            paymentClient.refund(order.getPaymentId());
        }
        return response(order, "Order cancelled successfully");
    }

    private OrderResponse response(Order order, String message) {
        return new OrderResponse(order.getId(), order.getStatus(), message, order.getTotalAmount());
    }
}
