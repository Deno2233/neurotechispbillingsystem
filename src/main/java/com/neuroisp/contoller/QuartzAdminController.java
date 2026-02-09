package com.neuroisp.contoller;

import lombok.RequiredArgsConstructor;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/quartz")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://192.168.100.3:4567",
        "http://192.168.15.26:8061"
})
public class QuartzAdminController {

    private final Scheduler scheduler;

    @GetMapping("/jobs")
    public List<Map<String, Object>> listJobs() throws SchedulerException {

        List<Map<String, Object>> jobs = new ArrayList<>();

        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

                Map<String, Object> jobInfo = new HashMap<>();
                jobInfo.put("jobName", jobKey.getName());
                jobInfo.put("jobGroup", jobKey.getGroup());

                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                List<String> triggerTimes = triggers.stream()
                        .map(trigger -> trigger.getNextFireTime().toString())
                        .toList();

                jobInfo.put("nextTriggerTimes", triggerTimes);

                jobs.add(jobInfo);
            }
        }
        return jobs;
    }
}
