package com.neuroisp.service;

import com.neuroisp.entity.*;
import com.neuroisp.repository.UserSubscriptionRepository;
import com.neuroisp.scheduler.QuartzSchedulerService;
import lombok.RequiredArgsConstructor;
import me.legrange.mikrotik.ApiConnection;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionCleanupScheduler {

    private final UserSubscriptionRepository subscriptionRepo;
    private final MikrotikService mikrotikService;
    private final QuartzSchedulerService quartzSchedulerService;
    @Scheduled(cron = "0 0 * * * *")
    public void safetyExpiryCheck() {

        subscriptionRepo.findByStatus(SubscriptionStatus.ACTIVE)
                .forEach(sub -> {
                    if (sub.getExpiryTime().isBefore(LocalDateTime.now())
                            && !sub.isCleanedUp()) {

                        try {
                            quartzSchedulerService.cancelSubscriptionExpiry(sub.getId());
                        } catch (Exception ignored) {
                        }
                    }
                });
    }
}