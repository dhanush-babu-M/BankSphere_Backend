package com.banksphere.modules.fixeddeposit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrematureFdClosureDTO {
    @NotNull
    private UUID fdId;
    
    @NotBlank
    private String reason;
    
    private UUID targetAccountId;
}
