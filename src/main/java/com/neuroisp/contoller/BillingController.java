package com.neuroisp.contoller;

import com.neuroisp.entity.BillingTransaction;
import com.neuroisp.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173,http://192.168.100.3:4567,http://192.168.15.26:8061")

public class BillingController {

    private final BillingService billingService;

    @GetMapping("/statement/{userId}")
    public List<BillingTransaction> statement(@PathVariable String userId) {
        return billingService.getStatement(userId);
    }

    @GetMapping("/balance/{userId}")
    public double balance(@PathVariable String userId) {
        return billingService.getBalance(userId);
    }
}
