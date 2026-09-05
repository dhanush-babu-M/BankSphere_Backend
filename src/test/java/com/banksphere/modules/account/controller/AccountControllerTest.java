package com.banksphere.modules.account.controller;

import com.banksphere.core.exception.GlobalExceptionHandler;
import com.banksphere.core.exception.ResourceNotFoundException;
import com.banksphere.core.security.CustomUserDetailsService;
import com.banksphere.core.security.jwt.JwtTokenProvider;
import com.banksphere.modules.account.dto.request.CreateAccountRequestDTO;
import com.banksphere.modules.account.dto.response.AccountBalanceResponseDTO;
import com.banksphere.modules.account.dto.response.AccountResponseDTO;
import com.banksphere.modules.account.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters during controller slice test
@ActiveProfiles("test")
@Import(GlobalExceptionHandler.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Get account by number should return 200 when account exists")
    void getAccountByNumber_shouldReturn200_whenAccountExists() throws Exception {
        AccountResponseDTO responseDTO = AccountResponseDTO.builder()
                .accountNumber("BSP2026123456789")
                .accountType("SAVINGS")
                .status("ACTIVE")
                .balance(new BigDecimal("5000.00"))
                .currency("INR")
                .build();

        when(accountService.getAccountByNumber("BSP2026123456789")).thenReturn(responseDTO);

        mockMvc.perform(get("/accounts/BSP2026123456789")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("BSP2026123456789"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.currency").value("INR"));
    }

    @Test
    @DisplayName("Get account by number should return 404 when account not found")
    void getAccountByNumber_shouldReturn404_whenAccountNotFound() throws Exception {
        when(accountService.getAccountByNumber("NON_EXISTENT"))
                .thenThrow(new ResourceNotFoundException("Account", "accountNumber", "NON_EXISTENT"));

        mockMvc.perform(get("/accounts/NON_EXISTENT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Get account balance should return 200 with balance info")
    void getAccountBalance_shouldReturn200_withBalanceInfo() throws Exception {
        AccountBalanceResponseDTO balanceDTO = AccountBalanceResponseDTO.builder()
                .accountId(UUID.randomUUID())
                .accountNumber("BSP2026123456789")
                .currentBalance(new BigDecimal("10000.00"))
                .availableBalance(new BigDecimal("9500.00"))
                .holdAmount(new BigDecimal("500.00"))
                .currency("INR")
                .asOfDateTime(LocalDateTime.now())
                .build();

        when(accountService.getAccountBalance("BSP2026123456789")).thenReturn(balanceDTO);

        mockMvc.perform(get("/accounts/BSP2026123456789/balance")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("BSP2026123456789"))
                .andExpect(jsonPath("$.currentBalance").value(10000.00))
                .andExpect(jsonPath("$.availableBalance").value(9500.00));
    }

    @Test
    @DisplayName("Create account should return 200 with valid request")
    void createAccount_shouldReturn200_withValidRequest() throws Exception {
        CreateAccountRequestDTO request = CreateAccountRequestDTO.builder()
                .customerId(UUID.randomUUID())
                .accountType("SAVINGS")
                .currency("INR")
                .branchCode("001")
                .initialDeposit(new BigDecimal("1000.00"))
                .build();

        AccountResponseDTO responseDTO = AccountResponseDTO.builder()
                .accountNumber("BSP2026123456789")
                .accountType("SAVINGS")
                .status("ACTIVE")
                .balance(new BigDecimal("1000.00"))
                .currency("INR")
                .build();

        when(accountService.createAccount(any(CreateAccountRequestDTO.class), eq("system")))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("BSP2026123456789"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
}

