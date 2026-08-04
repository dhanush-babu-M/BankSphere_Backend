package com.banksphere.core.notification.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsRequestDTO {
    private String to;
    private String message;
    private String templateId;
    private String senderId;
    private boolean isOtp;
    private String correlationId;
}
