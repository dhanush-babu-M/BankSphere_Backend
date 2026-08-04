package com.banksphere.modules.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MakerCheckerApprovalDTO {
    @NotNull
    private UUID operationId;
    @NotNull
    private boolean approved;
    private String comments;
}
