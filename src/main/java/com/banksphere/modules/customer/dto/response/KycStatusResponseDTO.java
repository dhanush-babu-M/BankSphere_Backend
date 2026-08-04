package com.banksphere.modules.customer.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class KycStatusResponseDTO {
    private UUID customerId;
    private String kycStatus;
    private LocalDateTime lastUpdated;
    private List<KycDocumentInfo> documents;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class KycDocumentInfo {
        private String documentType;
        private String documentNumber;
        private boolean verified;
        private LocalDateTime verifiedAt;
    }
}
