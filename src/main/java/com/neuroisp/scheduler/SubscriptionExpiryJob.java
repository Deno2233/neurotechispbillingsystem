package com.neuroisp.scheduler;

import com.neuroisp.entity.SubscriptionStatus;
import com.neuroisp.entity.UserSubscription;
import com.neuroisp.repository.UserSubscriptionRepository;
import com.neuroisp.service.MikrotikService;
import lombok.RequiredArgsConstructor;
import me.legrange.mikrotik.ApiConnection;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionExpiryJob implements Job {

    private final UserSubscriptionRepository subscriptionRepo;
    private final MikrotikService mikrotikService;

    @Override
    public void execute(JobExecutionContext context) {

        String subId = context.getMergedJobDataMap().getString("subscriptionId");

        UserSubscription sub =
                subscriptionRepo.findById(subId).orElse(null);

        if (sub == null || sub.isCleanedUp()) return;

        try (ApiConnection con = mikrotikService.connect(sub.getRouter())) {

            String username = sub.getUsername();

            // Remove active session
            var active = con.execute(
                    "/ip/hotspot/active/print where user=" + username
            );

            for (var row : active) {
                con.execute("/ip/hotspot/active/remove .id=" + row.get(".id"));
            }

            // Remove cookie
            var cookies = con.execute(
                    "/ip/hotspot/cookie/print where user=" + username
            );

            for (var row : cookies) {
                con.execute("/ip/hotspot/cookie/remove .id=" + row.get(".id"));
            }

            // Remove hotspot user
            var users = con.execute(
                    "/ip/hotspot/user/print where name=" + username
            );

            for (var row : users) {
                con.execute("/ip/hotspot/user/remove .id=" + row.get(".id"));
            }

            sub.setCleanedUp(true);
            sub.setStatus(SubscriptionStatus.EXPIRED);

            subscriptionRepo.save(sub);

        } catch (Exception e) {
            throw new RuntimeException("Quartz cleanup failed", e);
        }
    }
}
