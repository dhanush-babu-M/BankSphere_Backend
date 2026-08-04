package com.banksphere.modules.debitcard.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DebitCardPinChangeDTO {
    @NotNull
    private UUID cardId;

    @NotBlank
    @Size(min = 4, max = 4)
    private String currentPin;

    @NotBlank
    @Size(min = 4, max = 4)
    private String newPin;

    @NotBlank
    @Size(min = 4, max = 4)
    private String confirmPin;
}
