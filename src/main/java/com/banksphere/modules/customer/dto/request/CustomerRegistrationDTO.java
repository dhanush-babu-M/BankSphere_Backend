package com.banksphere.modules.customer.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CustomerRegistrationDTO {
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String phoneNumber;

    @NotNull
    private LocalDate dateOfBirth;

    private String gender;
    private String panNumber;
    private String aadharNumber;
    private String occupation;
    private BigDecimal annualIncome;
    private String address;
    private String city;
    private String state;
    private String pincode;

    @NotBlank
    @Size(min = 8)
    private String password;
}
