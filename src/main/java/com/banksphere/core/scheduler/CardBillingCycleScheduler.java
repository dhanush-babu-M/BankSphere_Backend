package com.banksphere.core.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CardBillingCycleScheduler {

    @Scheduled(cron = "0 0 0 1 * ?")
    public void generateCreditCardBills() {
        log.info("Starting generateCreditCardBills");
        // TODO: implement logic
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendPaymentDueReminders() {
        log.info("Starting sendPaymentDueReminders");
        // TODO: implement logic
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void applyLatePaymentCharges() {
        log.info("Starting applyLatePaymentCharges");
        // TODO: implement logic
    }
}
