package com.banksphere.core.notification.email.impl;

import com.banksphere.core.notification.email.EmailService;
import com.banksphere.core.notification.email.dto.EmailRequestDTO;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    @Override
    public void sendEmail(EmailRequestDTO request) {
        log.info("Sending email to {}", request.getTo());
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(request.getTo());
            if (request.getCc() != null && !request.getCc().isEmpty()) {
                helper.setCc(request.getCc().toArray(new String[0]));
            }
            if (request.getBcc() != null && !request.getBcc().isEmpty()) {
                helper.setBcc(request.getBcc().toArray(new String[0]));
            }
            helper.setSubject(request.getSubject());

            String content = request.getHtmlBody();
            if (content == null && request.getTemplateName() != null) {
                content = renderTemplate(request.getTemplateName(), request.getTemplateVariables());
            }

            if (content != null) {
                helper.setText(content, true);
            }
            
            // TODO: handle attachments and retry logic
            mailSender.send(message);
            log.info("Email sent successfully to {}", request.getTo());
        } catch (Exception e) {
            log.error("Failed to send email to {}", request.getTo(), e);
            throw new RuntimeException("Email sending failed", e);
        }
    }

    @Async
    @Override
    public void sendOtp(String to, String customerName, String otp) {
        EmailRequestDTO request = EmailRequestDTO.builder()
                .to(to)
                .subject("Your OTP Code")
                .templateName("otp-template")
                .templateVariables(Map.of("customerName", customerName, "otp", otp))
                .build();
        sendEmail(request);
    }

    @Async
    @Override
    public void sendTransactionAlert(String to, String customerName, String accountNumber, BigDecimal amount, String txnType, String referenceNumber) {
        EmailRequestDTO request = EmailRequestDTO.builder()
                .to(to)
                .subject("Transaction Alert")
                .templateName("transaction-template")
                .templateVariables(Map.of(
                        "customerName", customerName,
                        "accountNumber", accountNumber,
                        "amount", amount,
                        "txnType", txnType,
                        "referenceNumber", referenceNumber
                ))
                .build();
        sendEmail(request);
    }

    @Async
    @Override
    public void sendWelcomeEmail(String to, String customerName, String accountNumber, String customerId) {
        EmailRequestDTO request = EmailRequestDTO.builder()
                .to(to)
                .subject("Welcome to BankSphere")
                .templateName("welcome-template")
                .templateVariables(Map.of(
                        "customerName", customerName,
                        "accountNumber", accountNumber,
                        "customerId", customerId
                ))
                .build();
        sendEmail(request);
    }

    @Async
    @Override
    public void sendPasswordResetEmail(String to, String customerName, String resetLink) {
        EmailRequestDTO request = EmailRequestDTO.builder()
                .to(to)
                .subject("Password Reset Request")
                .templateName("password-reset-template")
                .templateVariables(Map.of("customerName", customerName, "resetLink", resetLink))
                .build();
        sendEmail(request);
    }

    @Async
    @Override
    public void sendLoanApprovalEmail(String to, String customerName, BigDecimal loanAmount, String loanId) {
        EmailRequestDTO request = EmailRequestDTO.builder()
                .to(to)
                .subject("Loan Approved")
                .templateName("loan-approval-template")
                .templateVariables(Map.of("customerName", customerName, "loanAmount", loanAmount, "loanId", loanId))
                .build();
        sendEmail(request);
    }

    private String renderTemplate(String name, Map<String, Object> vars) {
        Context context = new Context();
        if (vars != null) {
            context.setVariables(vars);
        }
        return templateEngine.process(name, context);
    }
}
