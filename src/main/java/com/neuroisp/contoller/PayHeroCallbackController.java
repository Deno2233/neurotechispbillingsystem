package com.neuroisp.contoller;

import com.neuroisp.service.PaymentTransactionService;
import com.neuroisp.service.UserSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@RequestMapping("/api/payhero")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173,http://192.168.100.3:4567,http://192.168.15.26:8061")

public class PayHeroCallbackController {

    private final UserSubscriptionService subscriptionService;
    private final PaymentTransactionService transactionService;

    @PostMapping("/callback")
    public String handleCallback(@RequestBody Map<String, Object> payload) {

        Map<String, Object> response = (Map<String, Object>) payload.get("response");

        String externalRef = response.get("ExternalReference").toString(); // Subscription ID
        String mpesaReceipt = response.get("MpesaReceiptNumber").toString();
        String status = response.get("Status").toString();
        int resultCode = Integer.parseInt(response.get("ResultCode").toString());
        double amount = Double.parseDouble(response.get("Amount").toString());

        // ✅ Save transaction always
        transactionService.logTransaction(
                externalRef,
                mpesaReceipt,
                status,
                amount
        );

        // ✅ Activate ONLY if payment successful
        if (resultCode == 0 && "Success".equalsIgnoreCase(status)) {
            subscriptionService.activateSubscription(externalRef, mpesaReceipt);
        }

        return "OK";
    }
}

