package com.banksphere.core.notification.template;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationTemplateEngine {

    private final TemplateEngine templateEngine;

    public String renderOtpEmail(String customerName, String otp, int expiryMinutes) {
        Context context = new Context();
        context.setVariable("customerName", customerName);
        context.setVariable("otp", otp);
        context.setVariable("expiryMinutes", expiryMinutes);
        return templateEngine.process("otp-email", context);
    }

    public String renderTransactionAlert(String customerName, String accountNumber, BigDecimal amount, String txnType, LocalDateTime txnTime) {
        Context context = new Context();
        context.setVariable("customerName", customerName);
        context.setVariable("accountNumber", accountNumber);
        context.setVariable("amount", amount);
        context.setVariable("txnType", txnType);
        context.setVariable("txnTime", txnTime);
        return templateEngine.process("transaction-alert", context);
    }

    public String renderWelcomeEmail(String customerName, String accountNumber, String customerId) {
        Context context = new Context();
        context.setVariable("customerName", customerName);
        context.setVariable("accountNumber", accountNumber);
        context.setVariable("customerId", customerId);
        return templateEngine.process("welcome-email", context);
    }

    public String renderAccountStatement(Map<String, Object> statementData) {
        Context context = new Context();
        context.setVariables(statementData);
        return templateEngine.process("account-statement", context);
    }
}
