package com.neuroisp.sms;

import com.neuroisp.entity.SmsProvider;
import com.neuroisp.entity.SmsProviderType;
import com.neuroisp.repository.SmsProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class BytewaveSmsSender implements SmsSender {

    private final RestTemplate restTemplate = new RestTemplate();
    private final SmsProviderRepository smsProviderRepo;

    @Override
    public void sendSms(String phone, String message) {
        System.out.println(phone);
        System.out.println(message);
        SmsProvider provider = smsProviderRepo .findByNameAndActiveTrue("BYTEWAVE") .orElseThrow(() -> new RuntimeException("Bytewave SMS not configured"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(
                java.util.List.of(MediaType.APPLICATION_JSON)
        );

        Map<String, Object> payload = Map.of(
                "api_token", provider.getApiKey(),          // ✅ TOKEN IN BODY
                "recipient", normalizePhone(phone),         // ✅ recipient
                "sender_id", provider.getSenderId(),        // ✅ approved sender
                "type", "plain",                             // ✅ required
                "message", message
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(payload, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                provider.getApiUrl(),
                request,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException(
                    "Bytewave SMS failed: " + response.getBody()
            );
        }
    }

    private String normalizePhone(String phone) {
        // Bytewave expects international format, no +
        if (phone.startsWith("0")) {
            return "254" + phone.substring(1);
        }
        if (phone.startsWith("+")) {
            return phone.substring(1);
        }
        return phone;
    }
}