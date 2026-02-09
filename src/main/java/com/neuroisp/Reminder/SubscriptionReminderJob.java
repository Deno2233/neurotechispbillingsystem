package com.neuroisp.Reminder;

import com.neuroisp.entity.IspCompany;
import com.neuroisp.entity.SubscriptionStatus;
import com.neuroisp.entity.UserSubscription;
import com.neuroisp.repository.UserSubscriptionRepository;
import com.neuroisp.service.IspCompanyService;
import com.neuroisp.sms.SmsService;
import com.neuroisp.sms.SubscriptionSmsBuilder;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionReminderJob implements Job {

    private final UserSubscriptionRepository subscriptionRepo;
    private final SubscriptionSmsBuilder smsBuilder;
    private final SmsService smsService;
    private final IspCompanyService ispCompanyService;

    @Override
    public void execute(JobExecutionContext context) {

        String subId = context.getMergedJobDataMap().getString("subscriptionId");
        UserSubscription sub = subscriptionRepo.findById(subId).orElse(null);
        if (sub == null || sub.getStatus() != SubscriptionStatus.ACTIVE) return;

        try {
            IspCompany isp = ispCompanyService.getActiveCompany();
            String sms = "Reminder: Your package " + sub.getInternetPackage().getName() +
                    " will expire at " + sub.getExpiryTime() +
                    ". Enjoy your internet!";
            smsService.send(isp, sub.getPhoneNumber(), sms);
        } catch (Exception ignored) {}
    }
}
