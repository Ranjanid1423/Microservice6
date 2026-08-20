package com.example.orderservice.dto;

public record ErrorResponse(
        int status,
        String message
) {
}