package com.banksphere.modules.customer.dto.request;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CustomerProfileUpdateDTO {
    private String presentAddress;
    private String permanentAddress;
    private String city;
    private String state;
    private String pincode;
    private String communicationPreference;
    private String preferredLanguage;
}
