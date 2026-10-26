package com.example.ecommerce.payment.repository;

import com.example.ecommerce.payment.model.Payment;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByOrderIdOrderByCreatedAtDesc(UUID orderId);
}
