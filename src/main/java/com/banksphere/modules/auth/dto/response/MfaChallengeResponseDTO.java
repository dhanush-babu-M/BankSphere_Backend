package com.banksphere.modules.auth.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MfaChallengeResponseDTO {
    private boolean mfaRequired;
    private String sessionToken;
    private String message;
}
