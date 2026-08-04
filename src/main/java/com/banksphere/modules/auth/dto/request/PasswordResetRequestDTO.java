package com.banksphere.modules.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequestDTO {
    @NotBlank
    private String token;
    @NotBlank
    @Size(min = 8)
    private String newPassword;
    @NotBlank
    private String confirmPassword;
}
