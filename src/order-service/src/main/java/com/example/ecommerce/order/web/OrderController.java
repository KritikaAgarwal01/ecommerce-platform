package com.example.ecommerce.order.web;

import com.example.ecommerce.order.model.Order;
import com.example.ecommerce.order.model.OrderDtos.CreateOrderRequest;
import com.example.ecommerce.order.model.OrderDtos.OrderResponse;
import com.example.ecommerce.order.model.OrderDtos.UpdateOrderStatusRequest;
import com.example.ecommerce.order.repository.OrderRepository;
import com.example.ecommerce.order.service.OrderFulfillmentService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderFulfillmentService service;
    private final OrderRepository repository;

    public OrderController(OrderFulfillmentService service, OrderRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @PostMapping
    ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    ResponseEntity<Order> get(@PathVariable UUID id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    List<Order> byUser(@PathVariable String userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @PutMapping("/{id}/status")
    ResponseEntity<OrderResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        OrderResponse response = service.updateStatus(id, request.status());
        return response == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    ResponseEntity<OrderResponse> cancel(@PathVariable UUID id) {
        OrderResponse response = service.cancel(id);
        return response == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(response);
    }
}
