package com.banksphere.modules.reports.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatementRequestDTO {
    @NotNull
    private UUID accountId;
    @NotNull
    private LocalDate fromDate;
    @NotNull
    private LocalDate toDate;
    @Builder.Default
    private String format = "JSON"; // PDF/EXCEL/JSON default JSON
    private String transactionType; // nullable filter
}
