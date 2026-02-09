package com.neuroisp.scheduler;

import com.neuroisp.service.PppoeSubscriptionService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@Configuration
public class BillingScheduler {

    private final PppoeSubscriptionService subscriptionService;

    public BillingScheduler(PppoeSubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    // Runs every 15 minutes
    @Scheduled(fixedRate = 900000)
    public void suspendUnpaidUsers() {
        subscriptionService.suspendExpiredUnpaid();
    }
}
