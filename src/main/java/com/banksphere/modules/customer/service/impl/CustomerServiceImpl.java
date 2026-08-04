package com.banksphere.modules.customer.service.impl;

import com.banksphere.core.exception.DuplicateResourceException;
import com.banksphere.core.exception.ResourceNotFoundException;
import com.banksphere.modules.auth.entity.User;
import com.banksphere.modules.auth.service.AuthService;
import com.banksphere.modules.customer.dto.request.*;
import com.banksphere.modules.customer.dto.response.*;
import com.banksphere.modules.customer.entity.*;
import com.banksphere.modules.customer.mapper.CustomerMapper;
import com.banksphere.modules.customer.repository.*;
import com.banksphere.modules.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final KycDocumentRepository kycDocumentRepository;
    private final CustomerMapper customerMapper;
    private final AuthService authService;

    @Override
    public CustomerResponseDTO registerCustomer(CustomerRegistrationDTO request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Customer", "email", request.getEmail());
        }
        if (request.getPanNumber() != null && customerRepository.existsByPanNumber(request.getPanNumber())) {
            throw new DuplicateResourceException("Customer", "panNumber", request.getPanNumber());
        }

        User user = authService.registerUser(request.getEmail(), request.getEmail(), request.getPassword(), List.of("ROLE_CUSTOMER"));
        String customerId = "BSP" + String.format("%08d", new Random().nextInt(99999999));

        Customer customer = Customer.builder()
                .customerId(customerId)
                .userId(user.getId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .panNumber(request.getPanNumber())
                .kycStatus("PENDING")
                .build();
        
        customer = customerRepository.save(customer);

        CustomerProfile profile = CustomerProfile.builder()
                .customerId(customer.getId())
                .presentAddress(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .build();
        customerProfileRepository.save(profile);

        log.info("Customer registered: {}", customer.getCustomerId());
        return customerMapper.toResponseDTO(customer);
    }

    @Override
    public CustomerResponseDTO getCustomer(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id.toString()));
        return customerMapper.toResponseDTO(customer);
    }

    @Override
    public CustomerResponseDTO getCustomerByCustomerId(String customerId) {
        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "customerId", customerId));
        return customerMapper.toResponseDTO(customer);
    }

    @Override
    public CustomerResponseDTO updateProfile(UUID customerId, CustomerProfileUpdateDTO request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId.toString()));
        
        CustomerProfile profile = customerProfileRepository.findByCustomerId(customerId)
                .orElseGet(() -> CustomerProfile.builder().customerId(customerId).build());

        if (request.getPresentAddress() != null) profile.setPresentAddress(request.getPresentAddress());
        if (request.getPermanentAddress() != null) profile.setPermanentAddress(request.getPermanentAddress());
        if (request.getCity() != null) profile.setCity(request.getCity());
        if (request.getState() != null) profile.setState(request.getState());
        if (request.getPincode() != null) profile.setPincode(request.getPincode());
        if (request.getCommunicationPreference() != null) profile.setCommunicationPreference(request.getCommunicationPreference());
        if (request.getPreferredLanguage() != null) profile.setPreferredLanguage(request.getPreferredLanguage());

        customerProfileRepository.save(profile);
        return customerMapper.toResponseDTO(customer);
    }

    @Override
    public KycStatusResponseDTO submitKycDocument(CustomerKycDTO request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId().toString()));

        KycDocument document = kycDocumentRepository.findByCustomerIdAndDocumentType(request.getCustomerId(), request.getDocumentType())
                .orElseGet(() -> KycDocument.builder()
                        .customerId(request.getCustomerId())
                        .documentType(request.getDocumentType())
                        .build());
        
        document.setDocumentNumber(request.getDocumentNumber());
        document.setDocumentPath(request.getDocumentFilePath());
        document.setVerified(false);
        kycDocumentRepository.save(document);

        if ("REJECTED".equals(customer.getKycStatus())) {
            customer.setKycStatus("PENDING");
            customerRepository.save(customer);
        }

        return getKycStatus(request.getCustomerId());
    }

    @Override
    public KycStatusResponseDTO getKycStatus(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId.toString()));
        
        List<KycDocument> documents = kycDocumentRepository.findByCustomerId(customerId);
        
        List<KycStatusResponseDTO.KycDocumentInfo> docInfos = documents.stream().map(doc -> {
            String maskedNumber = doc.getDocumentNumber() != null && doc.getDocumentNumber().length() > 4 
                ? "****" + doc.getDocumentNumber().substring(doc.getDocumentNumber().length() - 4) 
                : doc.getDocumentNumber();
            return KycStatusResponseDTO.KycDocumentInfo.builder()
                    .documentType(doc.getDocumentType())
                    .documentNumber(maskedNumber)
                    .verified(doc.isVerified())
                    .verifiedAt(doc.getVerifiedAt())
                    .build();
        }).toList();

        return KycStatusResponseDTO.builder()
                .customerId(customerId)
                .kycStatus(customer.getKycStatus())
                .lastUpdated(customer.getUpdatedAt())
                .documents(docInfos)
                .build();
    }

    @Override
    public void approveKyc(UUID customerId, String approvedBy) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId.toString()));
        
        List<KycDocument> documents = kycDocumentRepository.findByCustomerId(customerId);
        if (documents.isEmpty()) {
            throw new IllegalStateException("No KYC documents submitted");
        }

        LocalDateTime now = LocalDateTime.now();
        documents.forEach(doc -> {
            doc.setVerified(true);
            doc.setVerifiedBy(approvedBy);
            doc.setVerifiedAt(now);
        });
        kycDocumentRepository.saveAll(documents);

        customer.setKycStatus("VERIFIED");
        customer.setKycCompletedAt(now);
        customerRepository.save(customer);

        log.info("KYC approved for customer: {}", customerId);
    }

    @Override
    public void rejectKyc(UUID customerId, String reason, String rejectedBy) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId.toString()));
        
        customer.setKycStatus("REJECTED");
        customerRepository.save(customer);

        List<KycDocument> documents = kycDocumentRepository.findByCustomerId(customerId);
        if (!documents.isEmpty()) {
            KycDocument latestDoc = documents.get(documents.size() - 1);
            latestDoc.setRejectionReason(reason);
            kycDocumentRepository.save(latestDoc);
        }

        log.info("KYC rejected for customer: {}", customerId);
    }

    @Override
    public Page<CustomerResponseDTO> searchCustomers(String query, Pageable pageable) {
        // TODO: implement proper search by query
        log.info("Search customers query: {}", query);
        return customerRepository.findAll(pageable).map(customerMapper::toResponseDTO);
    }

    @Override
    public Page<CustomerResponseDTO> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable).map(customerMapper::toResponseDTO);
    }
}
