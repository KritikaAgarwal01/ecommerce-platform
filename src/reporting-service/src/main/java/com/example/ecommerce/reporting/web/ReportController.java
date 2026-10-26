package com.example.ecommerce.reporting.web;

import com.example.ecommerce.reporting.model.Reports.ActiveCartsReport;
import com.example.ecommerce.reporting.model.Reports.DailyOrderReport;
import com.example.ecommerce.reporting.model.Reports.OrderSummary;
import com.example.ecommerce.reporting.model.Reports.PaymentSummary;
import com.example.ecommerce.reporting.service.ReportingService;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportingService service;

    public ReportController(ReportingService service) {
        this.service = service;
    }

    @GetMapping("/orders/summary")
    OrderSummary orderSummary() {
        return service.getOrderSummary();
    }

    @GetMapping("/orders/daily")
    DailyOrderReport dailyOrders(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.getDailyOrderReport(date == null ? LocalDate.now() : date);
    }

    @GetMapping("/payments/summary")
    PaymentSummary paymentSummary() {
        return service.getPaymentSummary();
    }

    @GetMapping("/carts/active")
    ActiveCartsReport activeCarts() {
        return service.getActiveCartsReport();
    }
}
