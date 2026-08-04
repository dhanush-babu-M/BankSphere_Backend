package com.banksphere.modules.customer.service;

import com.banksphere.modules.customer.dto.request.CustomerKycDTO;
import com.banksphere.modules.customer.dto.request.CustomerProfileUpdateDTO;
import com.banksphere.modules.customer.dto.request.CustomerRegistrationDTO;
import com.banksphere.modules.customer.dto.response.CustomerResponseDTO;
import com.banksphere.modules.customer.dto.response.KycStatusResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomerService {
    CustomerResponseDTO registerCustomer(CustomerRegistrationDTO request);
    CustomerResponseDTO getCustomer(UUID id);
    CustomerResponseDTO getCustomerByCustomerId(String customerId);
    CustomerResponseDTO updateProfile(UUID id, CustomerProfileUpdateDTO request);
    KycStatusResponseDTO submitKycDocument(CustomerKycDTO request);
    KycStatusResponseDTO getKycStatus(UUID id);
    void approveKyc(UUID id, String approvedBy);
    void rejectKyc(UUID id, String reason, String rejectedBy);
    Page<CustomerResponseDTO> searchCustomers(String query, Pageable pageable);
    Page<CustomerResponseDTO> getAllCustomers(Pageable pageable);
}
