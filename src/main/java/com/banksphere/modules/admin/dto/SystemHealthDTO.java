package com.banksphere.modules.admin.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemHealthDTO {
    private String status;
    private String uptime;
    private String dbStatus;
    private String cacheStatus;
    private String schedulerStatus;
    private int activeUsers;
    private int totalTransactionsToday;
    private String systemVersion;
    private String javaVersion;
    private LocalDateTime timestamp;
}
