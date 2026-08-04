package com.banksphere.modules.employee.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCreateDTO {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String phoneNumber;
    
    private LocalDate dateOfBirth;
    private String gender;
    
    @NotBlank
    private String department;
    @NotBlank
    private String designation;
    
    private UUID branchId;
    private LocalDate joiningDate;
    private BigDecimal salary;
    private String panNumber;

    @NotBlank
    @Size(min = 8)
    private String password;
}
