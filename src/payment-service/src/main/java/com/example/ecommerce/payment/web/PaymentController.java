package com.example.ecommerce.payment.web;

import com.example.ecommerce.payment.model.Payment;
import com.example.ecommerce.payment.model.PaymentRequest;
import com.example.ecommerce.payment.model.PaymentResponse;
import com.example.ecommerce.payment.repository.PaymentRepository;
import com.example.ecommerce.payment.service.PaymentProcessor;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentProcessor processor;
    private final PaymentRepository repository;

    public PaymentController(PaymentProcessor processor, PaymentRepository repository) {
        this.processor = processor;
        this.repository = repository;
    }

    @PostMapping
    ResponseEntity<PaymentResponse> process(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(processor.process(request));
    }

    @GetMapping("/{id}")
    ResponseEntity<Payment> get(@PathVariable UUID id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/order/{orderId}")
    List<Payment> byOrder(@PathVariable UUID orderId) {
        return repository.findByOrderIdOrderByCreatedAtDesc(orderId);
    }

    @PostMapping("/{id}/refund")
    ResponseEntity<PaymentResponse> refund(@PathVariable UUID id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(processor.refund(id));
    }
}
