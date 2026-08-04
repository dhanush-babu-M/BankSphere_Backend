package com.banksphere.core.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AccountStatementScheduler {

    @Scheduled(cron = "0 0 2 1 * ?")
    public void generateMonthlyStatements() {
        log.info("Starting generateMonthlyStatements");
        // TODO: implement logic
    }

    @Scheduled(cron = "0 0 3 1 1,4,7,10 ?")
    public void generateQuarterlyStatements() {
        log.info("Starting generateQuarterlyStatements");
        // TODO: implement logic
    }
}
