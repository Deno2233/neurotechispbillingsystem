package com.neuroisp.contoller;

import com.neuroisp.entity.SubscriptionStatus;
import com.neuroisp.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://192.168.100.3:4567",
        "http://192.168.15.26:8061"
})
public class SubscriptionController {

    private final UserSubscriptionRepository subscriptionRepo;

    @GetMapping("/{id}/status")
    public SubscriptionStatus getStatus(@PathVariable String id) {
        return subscriptionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"))
                .getStatus();
    }
}
