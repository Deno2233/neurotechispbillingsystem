package com.neuroisp.service;

import com.neuroisp.entity.PaymentGateway;
import com.neuroisp.repository.PaymentGatewayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
public class PayHeroWalletService {

    private final PaymentGatewayRepository paymentGatewayRepository;

    // --- Get Service Wallet Balance ---
    public String getWalletBalance() throws Exception {
        PaymentGateway gateway = paymentGatewayRepository
                .findByNameAndActive("PAYHERO", true)
                .orElseThrow(() -> new RuntimeException("PayHero gateway not configured"));

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://backend.payhero.co.ke/api/v2/wallets?wallet_type=service_wallet"))
                .GET()
                .header("Authorization", "Basic " + gateway.getAuthorizationToken())
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    // --- Service Wallet Top-Up ---
    public String topUpWallet(String phoneNumber, double amount) throws Exception {
        PaymentGateway gateway = paymentGatewayRepository
                .findByNameAndActive("PAYHERO", true)
                .orElseThrow(() -> new RuntimeException("PayHero gateway not configured"));

        HttpClient client = HttpClient.newHttpClient();

        String requestBody = String.format("{\"amount\":%.2f,\"phone_number\":\"%s\"}", amount, phoneNumber);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://backend.payhero.co.ke/api/v2/topup?wallet_type=service_wallet"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Authorization", "Basic " + gateway.getAuthorizationToken())
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
