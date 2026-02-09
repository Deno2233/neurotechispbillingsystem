package com.neuroisp.contoller;

import com.neuroisp.service.PaymentSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/summary")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173,http://192.168.100.3:4567,http://192.168.15.26:8061")

public class PaymentSummaryController {

    private final PaymentSummaryService summaryService;

    @GetMapping
    public Map<String, Double> getSummary() {
        return Map.of(
                "daily", summaryService.todayTotal(),
                "weekly", summaryService.weeklyTotal(),
                "monthly", summaryService.monthlyTotal()
        );
    }
}
