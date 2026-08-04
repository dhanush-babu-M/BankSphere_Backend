package com.banksphere.modules.customer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CustomerKycDTO {
    @NotNull
    private UUID customerId;

    @NotBlank
    private String documentType;

    @NotBlank
    private String documentNumber;

    private String documentFilePath;
}
