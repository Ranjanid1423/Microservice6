package com.example.orderservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PaymentClient {

    private final RestClient restClient;

    public PaymentClient(
            RestClient.Builder restClientBuilder,
            @Value("${payment.service.url}") String paymentServiceUrl) {

        // Create request factory with timeout settings
        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        // Connection timeout: 3 seconds
        factory.setConnectTimeout(3000);

        // Read timeout: 6 seconds
        factory.setReadTimeout(6000);

        this.restClient = restClientBuilder
                .baseUrl(paymentServiceUrl)
                .requestFactory(factory)
                .build();
    }

    // =========================================================
    // NORMAL PAYMENT
    // =========================================================

    public String getPayment(Long paymentId) {

        return restClient
                .get()
                .uri("/api/payments/{paymentId}", paymentId)
                .retrieve()
                .body(String.class);
    }

    // =========================================================
    // SLOW PAYMENT
    // =========================================================

    public String getSlowPayment(int seconds) {

        return restClient
                .get()
                .uri("/api/payments/slow/{seconds}", seconds)
                .retrieve()
                .body(String.class);
    }
}