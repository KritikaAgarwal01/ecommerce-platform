package com.example.ecommerce.reporting.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public final class Reports {
    private Reports() {
    }

    public record OrderSummary(
            long totalOrders,
            BigDecimal totalRevenue,
            BigDecimal averageOrderValue,
            long completedOrders,
            long pendingOrders) {
    }

    public record DailyOrderReport(
            LocalDate orderDate,
            long orderCount,
            BigDecimal dailyRevenue) {
    }

    public record PaymentSummary(
            long totalPayments,
            BigDecimal totalProcessed,
            long successfulPayments,
            long failedPayments) {
    }

    public record ActiveCartsReport(long activeCartCount, Instant reportGeneratedAt) {
    }
}
