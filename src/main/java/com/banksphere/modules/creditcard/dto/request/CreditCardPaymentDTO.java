package com.banksphere.modules.creditcard.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreditCardPaymentDTO {
    @NotNull
    private UUID creditCardId;

    @NotNull
    private BigDecimal paymentAmount;

    private String paymentMethod;
    private String bankAccountNumber;
}
