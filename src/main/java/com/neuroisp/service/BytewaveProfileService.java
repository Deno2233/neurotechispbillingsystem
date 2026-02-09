package com.neuroisp.service;

import com.neuroisp.entity.SmsProvider;
import com.neuroisp.repository.SmsProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
@Service
@RequiredArgsConstructor
public class BytewaveProfileService {

    private final SmsProviderRepository smsProviderRepository;

    public String getProfile() throws Exception {

        SmsProvider provider = smsProviderRepository
                .findByNameAndActive("BYTEWAVE", true)
                .orElseThrow(() -> new RuntimeException("Bytewave SMS provider not configured"));

        String token = provider.getApiKey().trim();
        if (token.toLowerCase().startsWith("bearer ")) {
            token = token.substring(7);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://portal.bytewavenetworks.com/api/v3/balance"))
                .GET()
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response =
                HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}
