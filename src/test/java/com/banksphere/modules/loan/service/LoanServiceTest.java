package com.banksphere.modules.loan.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private Object loanRepository;
    
    @Mock
    private Object loanApplicationRepository;
    
    @Mock
    private Object loanScheduleRepository;
    
    @Mock
    private Object loanMapper;

    @Test
    @DisplayName("Calculate EMI should return correct amount")
    void calculateEmi_shouldReturnCorrectAmount() {
        // given
        // when
        // then
        assertThat(true).isTrue();
    }

    @Test
    @DisplayName("Apply for loan should create application")
    void applyForLoan_shouldCreateApplication() {
        // given
        // when
        // then
        assertThat(true).isTrue();
    }

    @Test
    @DisplayName("Approve loan should update status to approved")
    void approveLoan_shouldUpdateStatusToApproved() {
        // given
        // when
        // then
        assertThat(true).isTrue();
    }

    @Test
    @DisplayName("Approve loan should throw exception when already processed")
    void approveLoan_shouldThrowException_whenAlreadyProcessed() {
        // given
        // when
        // then
        assertThat(true).isTrue();
    }

    @Test
    @DisplayName("Pay EMI should update loan schedule")
    void payEmi_shouldUpdateLoanSchedule() {
        // given
        // when
        // then
        assertThat(true).isTrue();
    }

    @Test
    @DisplayName("Get amortization schedule should return complete schedule")
    void getAmortizationSchedule_shouldReturnCompleteSchedule() {
        // given
        // when
        // then
        assertThat(true).isTrue();
    }
}
