package com.example.orderservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.orderservice.dto.ErrorResponse;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.UserResponse;
import com.example.orderservice.model.Order;
import com.example.orderservice.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // =========================================================
    // GET ORDER + USER
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(
            @PathVariable Long id) {

        // Step 1: Find the order
        Order order = orderService.getOrderById(id);

        // Step 2: Order does not exist
        if (order == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        try {

            // Step 3: Get user ID from order
            Long userId = order.getUserId();

            // Step 4: Call User Service
            UserResponse user =
                    orderService.getUserFromUserService(userId);

            // Step 5: Create combined response
            OrderResponse response =
                    new OrderResponse(
                            order.getOrderId(),
                            order.getUserId(),
                            user
                    );

            // Step 6: Return successful response
            return ResponseEntity.ok(response);

        } catch (Exception e) {

            // Log the actual problem in Eclipse console
            System.out.println(
                    "User Service is unavailable: "
                    + e.getMessage()
            );

            // Create intentional error response
            ErrorResponse errorResponse =
                    new ErrorResponse(
                            503,
                            "User Service is currently unavailable"
                    );

            // Return HTTP 503
            return ResponseEntity
                    .status(503)
                    .body(errorResponse);
        }
    }

    // =========================================================
    // PAYMENT TEST
    // =========================================================

    @GetMapping("/{orderId}/payment/{paymentId}")
    public ResponseEntity<String> getPayment(
            @PathVariable Long orderId,
            @PathVariable Long paymentId) {

        try {

            String paymentResponse =
                    orderService.getPayment(paymentId);

            return ResponseEntity.ok(
                    "Order " + orderId
                    + " -> "
                    + paymentResponse
            );

        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .body(
                            "Payment Service call failed: "
                            + e.getClass().getSimpleName()
                    );
        }
    }

    // =========================================================
    // SLOW PAYMENT TEST
    // =========================================================

    @GetMapping("/{orderId}/slow-payment/{seconds}")
    public ResponseEntity<String> getSlowPayment(
            @PathVariable Long orderId,
            @PathVariable int seconds) {

        try {

            String paymentResponse =
                    orderService.getSlowPayment(seconds);

            return ResponseEntity.ok(
                    "Order " + orderId
                    + " -> "
                    + paymentResponse
            );

        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .body(
                            "Payment Service timed out or failed: "
                            + e.getClass().getSimpleName()
                    );
        }
    }
}