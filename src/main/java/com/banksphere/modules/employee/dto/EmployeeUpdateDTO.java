package com.banksphere.modules.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeUpdateDTO {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String department;
    private String designation;
    private UUID branchId;
    private BigDecimal salary;
    private Boolean active;
}
