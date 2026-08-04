package com.banksphere.core.notification.email.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestDTO {
    private String to;
    private List<String> cc;
    private List<String> bcc;
    private String subject;
    private String templateName;
    private Map<String, Object> templateVariables;
    private String htmlBody;
    private List<String> attachments;
    private int priority;
}
