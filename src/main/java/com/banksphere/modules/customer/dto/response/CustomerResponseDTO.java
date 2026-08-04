package com.banksphere.modules.customer.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CustomerResponseDTO {
    private UUID id;
    private String customerId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String gender;
    private String panNumber;
    private String kycStatus;
    private LocalDateTime createdAt;
    private int accountCount;
}
