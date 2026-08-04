package com.banksphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * BankSphere Banking Application Entry Point.
 * Enterprise-grade banking system built with Spring Boot 3.3.x and Java 21.
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableCaching
public class BankSphereApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankSphereApplication.class, args);
    }
}
