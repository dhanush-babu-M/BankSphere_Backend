package com.banksphere.modules.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MfaVerificationDTO {
    @NotBlank
    private String username;
    @NotBlank
    @Size(min = 6, max = 6)
    private String mfaCode;
}
