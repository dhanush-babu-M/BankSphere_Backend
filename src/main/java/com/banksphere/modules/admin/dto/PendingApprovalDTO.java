package com.banksphere.modules.admin.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingApprovalDTO {
    private UUID id;
    private String operationType;
    private String entityName;
    private String requestedBy;
    private LocalDateTime requestedAt;
    private String status;
    private String module;
}
