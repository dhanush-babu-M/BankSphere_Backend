package com.banksphere.modules.creditcard.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreditCardApplicationDTO {
    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotBlank(message = "Card type is required")
    private String cardType;

    private BigDecimal requestedCreditLimit;
    private BigDecimal annualIncome;
    private String occupation;

    @NotBlank(message = "Card holder name is required")
    private String cardHolderName;
}
