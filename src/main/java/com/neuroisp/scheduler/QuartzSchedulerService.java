package com.neuroisp.scheduler;

import com.neuroisp.Reminder.SubscriptionReminderJob;
import com.neuroisp.entity.UserSubscription;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class QuartzSchedulerService {

    private final Scheduler scheduler;

    public void scheduleSubscriptionExpiry(UserSubscription sub)
            throws SchedulerException {

        JobKey jobKey = JobKey.jobKey("sub-expiry-" + sub.getId());

        // Prevent duplicate scheduling
        if (scheduler.checkExists(jobKey)) return;

        JobDetail jobDetail = JobBuilder.newJob(SubscriptionExpiryJob.class)
                .withIdentity(jobKey)
                .usingJobData("subscriptionId", sub.getId())
                .storeDurably()
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger-sub-expiry-" + sub.getId())
                .startAt(Date.from(
                        sub.getExpiryTime()
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                ))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
        // Schedule expiry job as before
        scheduleSubscriptionExpiry(sub);

// Schedule reminder job 10 min before expiry
        Date reminderTime = Date.from(sub.getExpiryTime()
                .minusMinutes(10)
                .atZone(ZoneId.systemDefault())
                .toInstant());

        JobDetail reminderJob = JobBuilder.newJob(SubscriptionReminderJob.class)
                .withIdentity("sub-reminder-" + sub.getId())
                .usingJobData("subscriptionId", sub.getId())
                .storeDurably()
                .build();

        Trigger reminderTrigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger-sub-reminder-" + sub.getId())
                .startAt(reminderTime)
                .forJob(reminderJob)
                .build();

        scheduler.scheduleJob(reminderJob, reminderTrigger);

    }

    public void cancelSubscriptionExpiry(String subId)
            throws SchedulerException {

        scheduler.deleteJob(JobKey.jobKey("sub-expiry-" + subId));
    }
}
