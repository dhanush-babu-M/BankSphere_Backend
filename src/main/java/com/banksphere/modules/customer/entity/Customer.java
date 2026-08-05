package com.banksphere.modules.customer.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customers")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "customer_id", unique = true, nullable = false)
    private String customerId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender")
    private String gender;

    @Builder.Default
    @Column(name = "nationality")
    private String nationality = "INDIAN";

    @Column(name = "occupation")
    private String occupation;

    @Column(name = "annual_income")
    private BigDecimal annualIncome;

    @Column(name = "pan_number", unique = true)
    private String panNumber;

    @Column(name = "aadhar_number", unique = true)
    private String aadharNumber;

    @Builder.Default
    @Column(name = "kyc_status")
    private String kycStatus = "PENDING";

    @Column(name = "kyc_completed_at")
    private LocalDateTime kycCompletedAt;

    @Column(name = "user_id")
    private UUID userId;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
