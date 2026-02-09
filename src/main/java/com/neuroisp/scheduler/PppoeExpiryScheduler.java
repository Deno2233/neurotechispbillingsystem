package com.neuroisp.scheduler;



import com.neuroisp.entity.IspCompany;
import com.neuroisp.entity.PppoeSubscription;
import com.neuroisp.entity.SubscriptionStatus;
import com.neuroisp.repository.PppoeSubscriptionRepository;
import com.neuroisp.service.IspCompanyService;
import com.neuroisp.service.MikrotikPppoeService;

import com.neuroisp.sms.PppoeSmsMessageBuilder;
import com.neuroisp.sms.SmsService;
import com.neuroisp.sms.SubscriptionSmsBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PppoeExpiryScheduler {

    private final PppoeSubscriptionRepository repo;
    private final MikrotikPppoeService mikrotikService;
    private final SmsService smsService;
    private final SubscriptionSmsBuilder smsBuilder;
    private final IspCompanyService ispCompanyService;

    @Scheduled(fixedRate = 60000) // every 1 minute
    public void expireSubscriptions() {

        List<PppoeSubscription> expired =
                repo.findByStatusAndExpiryTimeBefore(
                        SubscriptionStatus.ACTIVE,
                        LocalDateTime.now()
                );

        for (PppoeSubscription sub : expired) {

            mikrotikService.disablePppoeUser(
                    sub.getRouter(),
                    sub.getUser().getUsername()
            );

            sub.setStatus(SubscriptionStatus.EXPIRED);
            repo.save(sub);

            IspCompany isp = ispCompanyService.getActiveCompany();

            smsService.send(
                    isp,
                    sub.getUser().getPhoneNumber(),
                    PppoeSmsMessageBuilder.buildExpiryMessage(isp, sub)
            );

        }
    }
}
