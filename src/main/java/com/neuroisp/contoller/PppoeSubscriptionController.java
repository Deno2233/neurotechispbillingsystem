package com.neuroisp.contoller;

import com.neuroisp.dto.ExtendExpiryRequest;
import com.neuroisp.dto.PppoeUserDetailsDTO;
import com.neuroisp.dto.PppoeUserViewDTO;
import com.neuroisp.entity.PaymentMethod;
import com.neuroisp.entity.PppoePackage;
import com.neuroisp.entity.PppoeSubscription;
import com.neuroisp.entity.PppoeUser;
import com.neuroisp.repository.PppoePackageRepository;
import com.neuroisp.repository.PppoeUserRepository;
import com.neuroisp.service.PppoeSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pppoe/subscriptions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173,http://192.168.100.3:4567,http://192.168.15.26:8061")

public class PppoeSubscriptionController {

    private final PppoeSubscriptionService subscriptionService;
    private final PppoeUserRepository userRepo;
    private final PppoePackageRepository packageRepo;
    @PostMapping("/create")
    public PppoeSubscription create(@RequestBody Map<String, String> body) {

        PppoeUser user = userRepo.findById(body.get("userId"))
                .orElseThrow(() -> new RuntimeException("User not found"));

        return subscriptionService.createPending(user, body.get("packageId"));
    }

    @PostMapping("/cash")
    public void cashPayment(@RequestBody Map<String, String> body) {

        PppoeUser user = userRepo.findById(body.get("userId"))
                .orElseThrow(() -> new RuntimeException("User not found"));

        double amount = Double.parseDouble(body.get("amount"));

        // ✅ THIS IS WHERE IT GOES
        subscriptionService.handlePaymentAndAutoActivate(
                user,
                amount,
                PaymentMethod.CASH,
                null
        );
    }
    @PostMapping("/mpesa")
    public void mpesaPayment(@RequestBody Map<String, String> body) {

        PppoeUser user = userRepo.findById(body.get("userId"))
                .orElseThrow(() -> new RuntimeException("User not found"));

        double amount = Double.parseDouble(body.get("amount"));
        String receipt = body.get("receipt");

        subscriptionService.handlePaymentAndAutoActivate(
                user,
                amount,
                PaymentMethod.MPESA,
                receipt
        );
    }


    @PutMapping("/activate")
    public PppoeSubscription activate(@PathVariable String id) {

        PppoeSubscription sub = subscriptionService
                .markAsPaid(id, null, false);

        return subscriptionService.activate(sub);
    }
    @PostMapping("/admin/create")
    public PppoeSubscription adminCreate(@RequestBody Map<String, Object> body) {

        PppoeUser user = userRepo.findById((String) body.get("userId"))
                .orElseThrow();

        PppoePackage pkg = packageRepo.findById((String) body.get("packageId"))
                .orElseThrow();

        boolean activateNow = Boolean.parseBoolean(
                body.getOrDefault("activateNow", "false").toString()
        );

        return subscriptionService.createByAdmin(user, pkg, activateNow);
    }
    @PutMapping("/activate/{id}")
    public PppoeSubscription activateLater(@PathVariable String id) {
        return subscriptionService.activateLater(id);
    }
    @PutMapping("/upgrade")
    public PppoeSubscription upgrade(@RequestBody Map<String, String> body) {
        return subscriptionService.upgradePackage(
                body.get("subscriptionId"),
                body.get("newPackageId")
        );
    }
    @GetMapping("/view")
    public List<PppoeUserViewDTO> getUsersWithService() {
        return subscriptionService.getUsersWithService();
    }
    @PostMapping("/mpesa/callback")
    public void mpesaCallback(@RequestBody Map<String, Object> payload) {

    /*
      Example payload you expect (simplified):
      {
        "phone": "254712345678",
        "amount": 1500,
        "receipt": "QWE123ABC"
      }
    */

        String phone = payload.get("phone").toString();
        double amount = Double.parseDouble(payload.get("amount").toString());
        String receipt = payload.get("receipt").toString();

        PppoeUser user = (PppoeUser) userRepo.findByPhoneNumber(phone)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ AUTO ACTIVATE WHEN BALANCE CLEARS
        subscriptionService.handlePaymentAndAutoActivate(
                user,
                amount,
                PaymentMethod.MPESA,
                receipt
        );
    }
    @GetMapping("/user/{userId}")
    public PppoeUserDetailsDTO getUserDetails(@PathVariable String userId) {
        return subscriptionService.getUserDetails(userId);
    }
    // ✅ Extend / Change expiry date
    @PutMapping("/{subscriptionId}/expiry")
    public ResponseEntity<PppoeSubscription> updateExpiry(
            @PathVariable String subscriptionId,
            @RequestBody ExtendExpiryRequest request
    ) {
        PppoeSubscription updated =
                subscriptionService.updateExpiryDate(
                        subscriptionId,
                        request.getNewExpiry()
                );

        return ResponseEntity.ok(updated);
    }
}
