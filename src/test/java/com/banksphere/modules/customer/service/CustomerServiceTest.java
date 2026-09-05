package com.banksphere.modules.customer.service;

import com.banksphere.core.exception.DuplicateResourceException;
import com.banksphere.core.exception.ResourceNotFoundException;
import com.banksphere.modules.auth.entity.User;
import com.banksphere.modules.auth.service.AuthService;
import com.banksphere.modules.customer.dto.request.CustomerRegistrationDTO;
import com.banksphere.modules.customer.dto.response.CustomerResponseDTO;
import com.banksphere.modules.customer.entity.Customer;
import com.banksphere.modules.customer.mapper.CustomerMapper;
import com.banksphere.modules.customer.repository.CustomerProfileRepository;
import com.banksphere.modules.customer.repository.CustomerRepository;
import com.banksphere.modules.customer.repository.KycDocumentRepository;
import com.banksphere.modules.customer.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerProfileRepository customerProfileRepository;

    @Mock
    private KycDocumentRepository kycDocumentRepository;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private AuthService authService;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer sampleCustomer;
    private CustomerResponseDTO sampleResponseDTO;

    @BeforeEach
    void setUp() {
        sampleCustomer = Customer.builder()
                .id(UUID.randomUUID())
                .customerId("BSP12345678")
                .userId(UUID.randomUUID())
                .firstName("Alice")
                .lastName("Smith")
                .email("alice@example.com")
                .phoneNumber("9876543210")
                .panNumber("ABCDE1234F")
                .kycStatus("PENDING")
                .build();

        sampleResponseDTO = CustomerResponseDTO.builder()
                .id(sampleCustomer.getId())
                .customerId(sampleCustomer.getCustomerId())
                .firstName(sampleCustomer.getFirstName())
                .lastName(sampleCustomer.getLastName())
                .email(sampleCustomer.getEmail())
                .kycStatus(sampleCustomer.getKycStatus())
                .build();
    }

    @Test
    @DisplayName("Register customer should create user and save customer entity")
    void registerCustomer_shouldCreateCustomerAndUser() {
        CustomerRegistrationDTO request = CustomerRegistrationDTO.builder()
                .firstName("Alice")
                .lastName("Smith")
                .email("alice@example.com")
                .password("Password@123")
                .phoneNumber("9876543210")
                .dateOfBirth(LocalDate.of(1995, 5, 20))
                .panNumber("ABCDE1234F")
                .address("123 Bank St")
                .city("Mumbai")
                .state("Maharashtra")
                .pincode("400001")
                .build();

        User mockUser = User.builder().id(UUID.randomUUID()).email("alice@example.com").build();

        when(customerRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(customerRepository.existsByPanNumber("ABCDE1234F")).thenReturn(false);
        when(authService.registerUser("alice@example.com", "alice@example.com", "Password@123", List.of("ROLE_CUSTOMER")))
                .thenReturn(mockUser);
        when(customerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));
        when(customerMapper.toResponseDTO(any(Customer.class))).thenReturn(sampleResponseDTO);

        CustomerResponseDTO response = customerService.registerCustomer(request);

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("alice@example.com");
        verify(customerRepository).save(any(Customer.class));
        verify(customerProfileRepository).save(any());
    }

    @Test
    @DisplayName("Register customer should throw DuplicateResourceException when email already registered")
    void registerCustomer_shouldThrowDuplicate_whenEmailExists() {
        CustomerRegistrationDTO request = CustomerRegistrationDTO.builder()
                .email("alice@example.com")
                .panNumber("ABCDE1234F")
                .build();

        when(customerRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> customerService.registerCustomer(request));
        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Get customer should return DTO when found")
    void getCustomer_shouldReturnDTO_whenFound() {
        UUID id = sampleCustomer.getId();
        when(customerRepository.findById(id)).thenReturn(Optional.of(sampleCustomer));
        when(customerMapper.toResponseDTO(sampleCustomer)).thenReturn(sampleResponseDTO);

        CustomerResponseDTO response = customerService.getCustomer(id);

        assertThat(response).isNotNull();
        assertThat(response.getCustomerId()).isEqualTo("BSP12345678");
    }

    @Test
    @DisplayName("Get customer should throw not found exception when not found")
    void getCustomer_shouldThrowNotFoundException_whenNotFound() {
        UUID randomId = UUID.randomUUID();
        when(customerRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.getCustomer(randomId));
    }

    @Test
    @DisplayName("Approve KYC should update KYC status to VERIFIED")
    void approveKyc_shouldUpdateKycStatus() {
        UUID id = sampleCustomer.getId();
        com.banksphere.modules.customer.entity.KycDocument doc = com.banksphere.modules.customer.entity.KycDocument.builder()
                .id(UUID.randomUUID())
                .customerId(id)
                .documentType("PAN")
                .documentNumber("ABCDE1234F")
                .verified(false)
                .build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(sampleCustomer));
        when(kycDocumentRepository.findByCustomerId(id)).thenReturn(List.of(doc));
        when(customerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));

        customerService.approveKyc(id, "OFFICER_1");

        assertThat(sampleCustomer.getKycStatus()).isEqualTo("VERIFIED");
        assertThat(doc.isVerified()).isTrue();
        verify(customerRepository).save(sampleCustomer);
        verify(kycDocumentRepository).saveAll(any());
    }

    @Test
    @DisplayName("Reject KYC should set rejected status with reason")
    void rejectKyc_shouldSetRejectedStatus() {
        UUID id = sampleCustomer.getId();
        when(customerRepository.findById(id)).thenReturn(Optional.of(sampleCustomer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));

        customerService.rejectKyc(id, "Blurry document image", "OFFICER_1");

        assertThat(sampleCustomer.getKycStatus()).isEqualTo("REJECTED");
        verify(customerRepository).save(sampleCustomer);
    }
}

