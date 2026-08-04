package com.banksphere.core.utility;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Year;

@Component
public class AccountNumberGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final String PREFIX = "BSP";

    public String generateAccountNumber() {
        int currentYear = Year.now().getValue();
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX).append(currentYear);
        for (int i = 0; i < 9; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public String generateVirtualAccountNumber(String prefix) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        int remainingLength = 16 - prefix.length();
        for (int i = 0; i < remainingLength; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public boolean isValidAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() != 16) {
            return false;
        }
        return accountNumber.startsWith(PREFIX);
    }
}
