package com.banksphere.modules.loan.service;

import com.banksphere.core.exception.ResourceNotFoundException;
import com.banksphere.modules.account.repository.AccountRepository;
import com.banksphere.modules.loan.dto.AmortizationScheduleDTO;
import com.banksphere.modules.loan.dto.LoanApplicationRequestDTO;
import com.banksphere.modules.loan.dto.LoanApprovalDTO;
import com.banksphere.modules.loan.dto.LoanResponseDTO;
import com.banksphere.modules.loan.entity.LoanApplication;
import com.banksphere.modules.loan.mapper.LoanMapper;
import com.banksphere.modules.loan.repository.LoanApplicationRepository;
import com.banksphere.modules.loan.repository.LoanCollateralRepository;
import com.banksphere.modules.loan.repository.LoanRepository;
import com.banksphere.modules.loan.repository.LoanScheduleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private LoanScheduleRepository loanScheduleRepository;

    @Mock
    private LoanCollateralRepository loanCollateralRepository;

    @Mock
    private LoanMapper loanMapper;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    @Test
    @DisplayName("Calculate EMI should return correct formula amount")
    void calculateEmi_shouldReturnCorrectAmount() {
        // Principal: 100,000, Annual Rate: 12%, Tenure: 12 months
        // Standard banking formula yields ~8,884.88
        BigDecimal emi = loanService.calculateEmi(
                new BigDecimal("100000"), new BigDecimal("12"), 12);

        assertThat(emi).isNotNull();
        assertThat(emi).isEqualByComparingTo("8884.88");
    }

    @Test
    @DisplayName("Calculate EMI with 0% interest rate should divide principal by months")
    void calculateEmi_withZeroInterest_shouldDividePrincipalByMonths() {
        BigDecimal emi = loanService.calculateEmi(
                new BigDecimal("120000"), BigDecimal.ZERO, 12);

        assertThat(emi).isEqualByComparingTo("10000.00");
    }

    @Test
    @DisplayName("Apply for loan should create application with SUBMITTED status")
    void applyForLoan_shouldCreateApplication() {
        LoanApplicationRequestDTO request = LoanApplicationRequestDTO.builder()
                .customerId(UUID.randomUUID())
                .loanType("PERSONAL")
                .requestedAmount(new BigDecimal("500000.00"))
                .requestedTenureMonths(24)
                .purpose("Home renovation")
                .annualIncome(new BigDecimal("1200000.00"))
                .existingEmi(BigDecimal.ZERO)
                .build();

        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenAnswer(i -> i.getArgument(0));

        LoanResponseDTO response = loanService.applyForLoan(request);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("SUBMITTED");
        assertThat(response.getSanctionedAmount()).isEqualByComparingTo("500000.00");
        verify(loanApplicationRepository).save(any(LoanApplication.class));
    }

    @Test
    @DisplayName("Approve loan should update status to APPROVED")
    void approveLoan_shouldUpdateStatusToApproved() {
        UUID appId = UUID.randomUUID();
        LoanApplication app = LoanApplication.builder()
                .id(appId)
                .applicationNumber("LAPP12345678")
                .requestedAmount(new BigDecimal("200000.00"))
                .requestedTenureMonths(12)
                .loanType("PERSONAL")
                .status("SUBMITTED")
                .build();

        LoanApprovalDTO request = LoanApprovalDTO.builder()
                .applicationId(appId)
                .approved(true)
                .sanctionedAmount(new BigDecimal("200000.00"))
                .interestRate(new BigDecimal("10.5"))
                .tenureMonths(12)
                .reviewNotes("Credit score is excellent")
                .build();

        when(loanApplicationRepository.findById(appId)).thenReturn(Optional.of(app));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenAnswer(i -> i.getArgument(0));
        when(loanRepository.save(any(com.banksphere.modules.loan.entity.Loan.class))).thenAnswer(i -> i.getArgument(0));

        LoanResponseDTO response = loanService.approveLoan(request, "MANAGER_1");

        assertThat(response).isNotNull();
        assertThat(app.getStatus()).isEqualTo("APPROVED");
        verify(loanApplicationRepository).save(app);
        verify(loanRepository).save(any(com.banksphere.modules.loan.entity.Loan.class));
        verify(loanScheduleRepository, times(12)).save(any(com.banksphere.modules.loan.entity.LoanSchedule.class));
    }

    @Test
    @DisplayName("Reject loan should update status to REJECTED")
    void approveLoan_withFalseApproval_shouldReject() {
        UUID appId = UUID.randomUUID();
        LoanApplication app = LoanApplication.builder()
                .id(appId)
                .applicationNumber("LAPP12345678")
                .status("SUBMITTED")
                .build();

        LoanApprovalDTO request = LoanApprovalDTO.builder()
                .applicationId(appId)
                .approved(false)
                .rejectionReason("Low credit score")
                .build();

        when(loanApplicationRepository.findById(appId)).thenReturn(Optional.of(app));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenAnswer(i -> i.getArgument(0));

        LoanResponseDTO response = loanService.approveLoan(request, "MANAGER_1");

        assertThat(response).isNotNull();
        assertThat(app.getStatus()).isEqualTo("REJECTED");
        verify(loanApplicationRepository).save(app);
    }

    @Test
    @DisplayName("Get amortization schedule should generate complete monthly schedule")
    void generateAmortizationSchedule_shouldReturnCompleteSchedule() {
        AmortizationScheduleDTO schedule = loanService.generateAmortizationSchedule(
                new BigDecimal("100000"), new BigDecimal("12"), 12);

        assertThat(schedule).isNotNull();
        assertThat(schedule.getScheduleItems()).hasSize(12);
        assertThat(schedule.getEmiAmount()).isEqualByComparingTo("8884.88");
        assertThat(schedule.getScheduleItems().get(0).getInstallmentNumber()).isEqualTo(1);
        assertThat(schedule.getScheduleItems().get(11).getInstallmentNumber()).isEqualTo(12);
    }
}

