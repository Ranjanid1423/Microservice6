package com.example.orderservice.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.orderservice.dto.UserResponse;
import com.example.orderservice.exception.UserServiceException;
import com.example.orderservice.model.Order;

@Service
public class OrderService {

    private final List<Order> orders = new ArrayList<>();

    private final RestClient userRestClient;

    private final PaymentClient paymentClient;

    public OrderService(
            RestClient.Builder restClientBuilder,
            PaymentClient paymentClient,
            @Value("${user.service.url}") String userServiceUrl) {

        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        this.userRestClient = restClientBuilder
                .baseUrl(userServiceUrl)
                .requestFactory(requestFactory)
                .build();

        this.paymentClient = paymentClient;

        // Sample orders
        orders.add(new Order(101L, 1L));
        orders.add(new Order(102L, 2L));
        orders.add(new Order(103L, 3L));
    }

    // =========================================================
    // FIND ORDER
    // =========================================================

    public Order getOrderById(Long orderId) {

        for (Order order : orders) {

            if (order.getOrderId().equals(orderId)) {
                return order;
            }
        }

        return null;
    }

    // =========================================================
    // CALL USER SERVICE
    // =========================================================

    public UserResponse getUserFromUserService(Long userId) {

        try {

            return userRestClient
                    .get()
                    .uri("/users/{id}", userId)
                    .retrieve()
                    .body(UserResponse.class);

        } catch (Exception e) {

            throw new UserServiceException(
                    "Unable to communicate with User Service",
                    e
            );
        }
    }
    // =========================================================
    // CALL PAYMENT SERVICE
    // =========================================================

    public String getPayment(Long paymentId) {

        return paymentClient.getPayment(paymentId);
    }

    // =========================================================
    // CALL SLOW PAYMENT
    // =========================================================

    public String getSlowPayment(int seconds) {

        return paymentClient.getSlowPayment(seconds);
    }
}