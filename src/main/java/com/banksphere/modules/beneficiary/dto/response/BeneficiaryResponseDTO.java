package com.banksphere.modules.beneficiary.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BeneficiaryResponseDTO {
    private UUID id;
    private UUID ownerCustomerId;
    private String beneficiaryName;
    private String accountNumber; // masked - last 4 digits
    private String bankName;
    private String ifscCode;
    private String accountType;
    private String nickname;
    private boolean verified;
    private boolean active;
    private BigDecimal dailyTransferLimit;
    private LocalDateTime addedAt;
}
