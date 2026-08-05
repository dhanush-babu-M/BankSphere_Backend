package com.banksphere.modules.beneficiary.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "beneficiaries")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Beneficiary {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "owner_customer_id", nullable = false)
    private UUID ownerCustomerId;

    @Column(name = "beneficiary_name", nullable = false)
    private String beneficiaryName;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "ifsc_code")
    private String ifscCode;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "nickname")
    private String nickname;

    @Builder.Default
    @Column(name = "verified")
    private boolean verified = false;

    @Builder.Default
    @Column(name = "active")
    private boolean active = true;

    @Column(name = "daily_transfer_limit", precision = 19, scale = 2)
    private BigDecimal dailyTransferLimit;

    @CreatedDate
    @Column(name = "added_at", updatable = false)
    private LocalDateTime addedAt;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
