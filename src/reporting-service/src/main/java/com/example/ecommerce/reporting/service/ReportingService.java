package com.example.ecommerce.reporting.service;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.example.ecommerce.reporting.model.Reports.ActiveCartsReport;
import com.example.ecommerce.reporting.model.Reports.DailyOrderReport;
import com.example.ecommerce.reporting.model.Reports.OrderSummary;
import com.example.ecommerce.reporting.model.Reports.PaymentSummary;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReportingService {
    private final JdbcTemplate jdbcTemplate;
    private final CosmosContainer cosmosContainer;

    public ReportingService(JdbcTemplate jdbcTemplate, CosmosContainer reportingContainer) {
        this.jdbcTemplate = jdbcTemplate;
        this.cosmosContainer = reportingContainer;
    }

    public OrderSummary getOrderSummary() {
        return jdbcTemplate.queryForObject("""
                SELECT COUNT(*) AS totalOrders,
                       COALESCE(SUM(totalAmount), 0) AS totalRevenue,
                       COALESCE(AVG(totalAmount), 0) AS averageOrderValue,
                       COUNT(CASE WHEN status = 'Completed' THEN 1 END) AS completedOrders,
                       COUNT(CASE WHEN status = 'Pending' THEN 1 END) AS pendingOrders
                FROM orders.dbo.Orders
                """, (result, row) -> new OrderSummary(
                result.getLong("totalOrders"),
                result.getBigDecimal("totalRevenue"),
                result.getBigDecimal("averageOrderValue"),
                result.getLong("completedOrders"),
                result.getLong("pendingOrders")));
    }

    public DailyOrderReport getDailyOrderReport(LocalDate date) {
        var reports = jdbcTemplate.query("""
                SELECT CAST(createdAt AS DATE) AS orderDate,
                       COUNT(*) AS orderCount,
                       COALESCE(SUM(totalAmount), 0) AS dailyRevenue
                FROM orders.dbo.Orders
                WHERE CAST(createdAt AS DATE) = ?
                GROUP BY CAST(createdAt AS DATE)
                """, (result, row) -> new DailyOrderReport(
                result.getDate("orderDate").toLocalDate(),
                result.getLong("orderCount"),
                result.getBigDecimal("dailyRevenue")), Date.valueOf(date));
        return reports.isEmpty() ? new DailyOrderReport(date, 0, BigDecimal.ZERO) : reports.getFirst();
    }

    public PaymentSummary getPaymentSummary() {
        return jdbcTemplate.queryForObject("""
                SELECT COUNT(*) AS totalPayments,
                       COALESCE(SUM(amount), 0) AS totalProcessed,
                       COUNT(CASE WHEN status = 'Succeeded' THEN 1 END) AS successfulPayments,
                       COUNT(CASE WHEN status = 'Failed' THEN 1 END) AS failedPayments
                FROM payments.dbo.Payments
                """, (result, row) -> new PaymentSummary(
                result.getLong("totalPayments"),
                result.getBigDecimal("totalProcessed"),
                result.getLong("successfulPayments"),
                result.getLong("failedPayments")));
    }

    public ActiveCartsReport getActiveCartsReport() {
        var results = cosmosContainer.queryItems(
                        "SELECT VALUE COUNT(1) FROM c WHERE ARRAY_LENGTH(c.items) > 0",
                        new CosmosQueryRequestOptions(), Long.class)
                .stream()
                .toList();
        long count = results.isEmpty() ? 0 : results.getFirst();
        return new ActiveCartsReport(count, Instant.now());
    }
}
