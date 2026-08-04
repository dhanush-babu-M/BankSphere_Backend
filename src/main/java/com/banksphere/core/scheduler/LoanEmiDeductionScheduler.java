package com.banksphere.core.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoanEmiDeductionScheduler {

    @Scheduled(cron = "0 0 9 * * MON-FRI")
    public void processEmiDeductions() {
        log.info("Starting processEmiDeductions");
        // TODO: implement logic
    }

    @Scheduled(cron = "0 0 8 * * MON-FRI")
    public void sendEmiReminders() {
        log.info("Starting sendEmiReminders");
        // TODO: implement logic
    }

    @Scheduled(cron = "0 0 10 * * MON-FRI")
    public void handleFailedEmis() {
        log.info("Starting handleFailedEmis");
        // TODO: implement logic
    }
}
