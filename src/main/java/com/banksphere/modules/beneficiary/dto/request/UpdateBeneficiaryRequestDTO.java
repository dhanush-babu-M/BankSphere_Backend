package com.banksphere.modules.beneficiary.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateBeneficiaryRequestDTO {
    private String nickname;
    private Boolean active;
    private BigDecimal dailyTransferLimit;
}
