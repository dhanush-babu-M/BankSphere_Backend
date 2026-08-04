package com.banksphere.modules.admin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pending_operations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String operationType;
    private String entityName;
    private String entityId;
    @Lob
    private String requestData;
    private String requestedBy;
    private LocalDateTime requestedAt;
    private String status;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private String comments;
    private String module;
}
