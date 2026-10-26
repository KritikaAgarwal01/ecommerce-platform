package com.example.ecommerce.cart.model;

import jakarta.validation.constraints.Min;

public record UpdateQuantityRequest(@Min(0) int quantity) {
}
