package com.banksphere.modules.account.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "account_holds")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountHold {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private UUID accountId;
    @Column(nullable = false)
    private BigDecimal holdAmount;
    private String holdReason;
    private String holdReference;
    private LocalDateTime expiryDateTime;
    @Column(nullable = false)
    private boolean released;
    private LocalDateTime releasedAt;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
