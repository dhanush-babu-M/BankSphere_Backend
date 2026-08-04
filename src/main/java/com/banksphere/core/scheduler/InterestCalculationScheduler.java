package com.banksphere.core.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class InterestCalculationScheduler {

    @Scheduled(cron = "0 0 0 1 * ?") // 1st of every month midnight
    public void calculateSavingsInterest() {
        log.info("Starting calculateSavingsInterest");
        try {
            // TODO: implement savings interest calculation
            logSchedulerRun("calculateSavingsInterest", true);
        } catch (Exception e) {
            log.error("Error in calculateSavingsInterest", e);
            logSchedulerRun("calculateSavingsInterest", false);
        }
    }

    @Scheduled(cron = "0 0 0 * * ?") // daily midnight
    public void calculateOverdraftInterest() {
        log.info("Starting calculateOverdraftInterest");
        try {
            // TODO: implement overdraft interest calculation
            logSchedulerRun("calculateOverdraftInterest", true);
        } catch (Exception e) {
            log.error("Error in calculateOverdraftInterest", e);
            logSchedulerRun("calculateOverdraftInterest", false);
        }
    }

    private void logSchedulerRun(String schedulerName, boolean success) {
        log.info("Scheduler {} finished with success: {}", schedulerName, success);
    }
}
