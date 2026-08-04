package com.banksphere.modules.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditReportDTO {
    private String reportId;
    private LocalDateTime generatedAt;
    private String generatedBy;
    private String reportType;
    private LocalDate fromDate;
    private LocalDate toDate;
    private int totalRecords;
    private Map<String, Object> summary;
    private List<Map<String, Object>> records;
}
