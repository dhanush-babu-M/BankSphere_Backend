package com.banksphere.modules.account.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatusUpdateRequestDTO {
    @NotNull
    private UUID accountId;
    @NotBlank
    private String newStatus;
    @NotBlank
    private String reason;
}
