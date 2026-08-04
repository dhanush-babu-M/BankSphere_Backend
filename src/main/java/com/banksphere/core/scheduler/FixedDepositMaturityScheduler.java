package com.banksphere.core.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class FixedDepositMaturityScheduler {

    @Scheduled(cron = "0 0 8 * * ?")
    public void processMaturedFDs() {
        log.info("Starting processMaturedFDs");
        // TODO: implement logic
    }

    @Scheduled(cron = "0 0 7 * * ?")
    public void sendMaturityReminderNotifications() {
        log.info("Starting sendMaturityReminderNotifications");
        // TODO: implement logic
    }
}
