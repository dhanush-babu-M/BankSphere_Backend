package com.banksphere.modules.loan.service;

import com.banksphere.core.exception.ResourceNotFoundException;
import com.banksphere.core.exception.InvalidTransactionException;
import com.banksphere.modules.account.repository.AccountRepository;
import com.banksphere.modules.account.entity.Account;
import com.banksphere.modules.loan.dto.*;
import com.banksphere.modules.loan.entity.*;
import com.banksphere.modules.loan.mapper.LoanMapper;
import com.banksphere.modules.loan.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanScheduleRepository loanScheduleRepository;
    private final LoanCollateralRepository loanCollateralRepository;
    private final LoanMapper loanMapper;
    private final AccountRepository accountRepository;

    @Override
    public LoanResponseDTO applyForLoan(LoanApplicationRequestDTO request) {
        String applicationNumber = "LAPP" + String.format("%08d", new Random().nextInt(99999999));

        LoanApplication app = LoanApplication.builder()
                .applicationNumber(applicationNumber)
                .customerId(request.getCustomerId())
                .loanType(request.getLoanType())
                .requestedAmount(request.getRequestedAmount())
                .requestedTenureMonths(request.getRequestedTenureMonths())
                .purpose(request.getPurpose())
                .annualIncome(request.getAnnualIncome())
                .existingEmi(request.getExistingEmi())
                .status("SUBMITTED")
                .build();

        app = loanApplicationRepository.save(app);

        log.info("Loan application submitted: {}", applicationNumber);
        
        return LoanResponseDTO.builder()
                .loanId(app.getApplicationNumber())
                .loanType(app.getLoanType())
                .sanctionedAmount(app.getRequestedAmount())
                .status(app.getStatus())
                .build();
    }

    @Override
    public LoanResponseDTO getLoanApplication(UUID id) {
        LoanApplication app = loanApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LoanApplication", "id", id.toString()));
        return LoanResponseDTO.builder()
                .loanId(app.getApplicationNumber())
                .loanType(app.getLoanType())
                .sanctionedAmount(app.getRequestedAmount())
                .status(app.getStatus())
                .build();
    }

    @Override
    public LoanResponseDTO approveLoan(LoanApprovalDTO request, String approvedBy) {
        LoanApplication application = loanApplicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("LoanApplication", "id", request.getApplicationId().toString()));

        LocalDateTime now = LocalDateTime.now();
        if (!request.isApproved()) {
            application.setStatus("REJECTED");
            application.setReviewedBy(approvedBy);
            application.setReviewedAt(now);
            application.setReviewNotes(request.getReviewNotes());
            application.setRejectionReason(request.getRejectionReason());
            loanApplicationRepository.save(application);
            return LoanResponseDTO.builder()
                    .loanId(application.getApplicationNumber())
                    .status(application.getStatus())
                    .build();
        }

        application.setStatus("APPROVED");
        application.setReviewedBy(approvedBy);
        application.setReviewedAt(now);
        loanApplicationRepository.save(application);

        String loanId = "LOAN" + String.format("%08d", new Random().nextInt(99999999));
        BigDecimal emiAmount = calculateEmi(request.getSanctionedAmount(), request.getInterestRate(), request.getTenureMonths());

        Loan loan = Loan.builder()
                .applicationId(application.getId())
                .customerId(application.getCustomerId())
                .loanId(loanId)
                .loanType(application.getLoanType())
                .sanctionedAmount(request.getSanctionedAmount())
                .interestRate(request.getInterestRate())
                .tenureMonths(request.getTenureMonths())
                .emiAmount(emiAmount)
                .processingFee(request.getSanctionedAmount().multiply(new BigDecimal("0.01")))
                .outstandingPrincipal(request.getSanctionedAmount())
                .outstandingInterest(BigDecimal.ZERO)
                .totalEmisCount(request.getTenureMonths())
                .emisPaid(0)
                .emisRemaining(request.getTenureMonths())
                .status("APPROVED")
                .disbursementAccountId(request.getDisbursementAccountId())
                .build();

        loan = loanRepository.save(loan);
        generateAndSaveSchedule(loan, request.getSanctionedAmount(), request.getInterestRate(), request.getTenureMonths());

        return LoanResponseDTO.builder()
                .loanId(loan.getLoanId())
                .loanType(loan.getLoanType())
                .sanctionedAmount(loan.getSanctionedAmount())
                .status(loan.getStatus())
                .build();
    }

    private void generateAndSaveSchedule(Loan loan, BigDecimal principal, BigDecimal annualRate, int months) {
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        BigDecimal outstanding = principal;
        LocalDate today = LocalDate.now();

        for (int i = 1; i <= months; i++) {
            BigDecimal interestForMonth = outstanding.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalForMonth = loan.getEmiAmount().subtract(interestForMonth);

            LoanSchedule schedule = LoanSchedule.builder()
                    .loanId(loan.getId())
                    .installmentNumber(i)
                    .dueDate(today.plusMonths(i))
                    .principalAmount(principalForMonth)
                    .interestAmount(interestForMonth)
                    .totalAmount(loan.getEmiAmount())
                    .status("PENDING")
                    .paidAmount(BigDecimal.ZERO)
                    .build();

            loanScheduleRepository.save(schedule);
            outstanding = outstanding.subtract(principalForMonth);
        }
    }

    @Override
    public LoanResponseDTO disburseLoan(UUID loanId, String disbursedBy) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", "id", loanId.toString()));
        
        if (!"APPROVED".equals(loan.getStatus())) {
            throw new IllegalStateException("Loan status is not APPROVED");
        }

        loan.setStatus("DISBURSED");
        loan.setDisbursedAt(LocalDateTime.now());
        loan.setNextEmiDate(LocalDate.now().plusMonths(1));
        
        Account acc = accountRepository.findById(loan.getDisbursementAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", loan.getDisbursementAccountId().toString()));
        acc.setAvailableBalance(acc.getAvailableBalance().add(loan.getSanctionedAmount()));
        accountRepository.save(acc);
        
        loanRepository.save(loan);
        log.info("Loan disbursed: {}", loan.getLoanId());

        return LoanResponseDTO.builder()
                .loanId(loan.getLoanId())
                .loanType(loan.getLoanType())
                .sanctionedAmount(loan.getSanctionedAmount())
                .status(loan.getStatus())
                .build();
    }

    @Override
    public Page<LoanResponseDTO> getCustomerLoans(UUID customerId, Pageable pageable) {
        return loanRepository.findByCustomerId(customerId, pageable).map(loan -> LoanResponseDTO.builder()
                .loanId(loan.getLoanId())
                .loanType(loan.getLoanType())
                .sanctionedAmount(loan.getSanctionedAmount())
                .status(loan.getStatus())
                .build());
    }

    @Override
    public LoanResponseDTO getLoan(UUID id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", "id", id.toString()));
        return LoanResponseDTO.builder()
                .loanId(loan.getLoanId())
                .loanType(loan.getLoanType())
                .sanctionedAmount(loan.getSanctionedAmount())
                .status(loan.getStatus())
                .build();
    }

    @Override
    public EmiRepaymentResponseDTO payEmi(EmiPaymentRequestDTO request) {
        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new ResourceNotFoundException("Loan", "id", request.getLoanId().toString()));

        Account account = accountRepository.findById(request.getPaymentAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.getPaymentAccountId().toString()));

        if (account.getAvailableBalance().compareTo(loan.getEmiAmount()) < 0) {
            throw new InvalidTransactionException("Insufficient balance for EMI payment");
        }

        List<LoanSchedule> pendingSchedules = loanScheduleRepository.findByLoanIdAndStatus(loan.getId(), "PENDING");
        if (pendingSchedules.isEmpty()) {
            throw new InvalidTransactionException("No pending EMI found");
        }

        LoanSchedule schedule = pendingSchedules.get(0);
        schedule.setPaidAmount(schedule.getTotalAmount());
        schedule.setStatus("PAID");
        schedule.setPaidDate(LocalDate.now());
        loanScheduleRepository.save(schedule);

        account.setAvailableBalance(account.getAvailableBalance().subtract(loan.getEmiAmount()));
        accountRepository.save(account);

        loan.setEmisPaid(loan.getEmisPaid() + 1);
        loan.setEmisRemaining(loan.getEmisRemaining() - 1);
        loan.setOutstandingPrincipal(loan.getOutstandingPrincipal().subtract(schedule.getPrincipalAmount()));
        loan.setNextEmiDate(schedule.getDueDate().plusMonths(1));

        if (loan.getEmisRemaining() == 0) {
            loan.setStatus("CLOSED");
            loan.setClosedAt(LocalDateTime.now());
        }

        loanRepository.save(loan);

        return EmiRepaymentResponseDTO.builder()
                .loanId(loan.getId())
                .installmentNumber(schedule.getInstallmentNumber())
                .paidAmount(schedule.getPaidAmount())
                .principalPaid(schedule.getPrincipalAmount())
                .interestPaid(schedule.getInterestAmount())
                .outstandingAfterPayment(loan.getOutstandingPrincipal())
                .nextEmiDate(loan.getNextEmiDate())
                .transactionReference(UUID.randomUUID().toString())
                .build();
    }

    @Override
    public AmortizationScheduleDTO getAmortizationSchedule(UUID loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", "id", loanId.toString()));
        
        List<LoanSchedule> schedules = loanScheduleRepository.findByLoanIdOrderByInstallmentNumber(loanId);
        
        List<AmortizationScheduleDTO.LoanScheduleItem> items = schedules.stream().map(s -> AmortizationScheduleDTO.LoanScheduleItem.builder()
                .installmentNumber(s.getInstallmentNumber())
                .dueDate(s.getDueDate())
                .principalAmount(s.getPrincipalAmount())
                .interestAmount(s.getInterestAmount())
                .totalAmount(s.getTotalAmount())
                .outstandingBalance(BigDecimal.ZERO)
                .build()).collect(Collectors.toList());

        return AmortizationScheduleDTO.builder()
                .loanId(loan.getId())
                .emiAmount(loan.getEmiAmount())
                .totalAmount(loan.getEmiAmount().multiply(BigDecimal.valueOf(loan.getTotalEmisCount())))
                .scheduleItems(items)
                .build();
    }

    @Override
    public AmortizationScheduleDTO generateAmortizationSchedule(BigDecimal principal, BigDecimal rate, int months) {
        BigDecimal emiAmount = calculateEmi(principal, rate, months);
        
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        BigDecimal outstanding = principal;
        LocalDate today = LocalDate.now();
        List<AmortizationScheduleDTO.LoanScheduleItem> items = new java.util.ArrayList<>();

        for (int i = 1; i <= months; i++) {
            BigDecimal interestForMonth = outstanding.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalForMonth = emiAmount.subtract(interestForMonth);
            
            items.add(AmortizationScheduleDTO.LoanScheduleItem.builder()
                    .installmentNumber(i)
                    .dueDate(today.plusMonths(i))
                    .principalAmount(principalForMonth)
                    .interestAmount(interestForMonth)
                    .totalAmount(emiAmount)
                    .outstandingBalance(outstanding.subtract(principalForMonth))
                    .build());

            outstanding = outstanding.subtract(principalForMonth);
        }

        return AmortizationScheduleDTO.builder()
                .loanId(null)
                .emiAmount(emiAmount)
                .totalAmount(emiAmount.multiply(BigDecimal.valueOf(months)))
                .scheduleItems(items)
                .build();
    }

    @Override
    public BigDecimal calculateEmi(BigDecimal principal, BigDecimal rate, int months) {
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
        }
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal onePlusRN = onePlusR.pow(months);
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(onePlusRN);
        BigDecimal denominator = onePlusRN.subtract(BigDecimal.ONE);
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    @Override
    public void closeLoan(UUID loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", "id", loanId.toString()));
        if (loan.getOutstandingPrincipal().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Cannot close loan with outstanding balance");
        }
        loan.setStatus("CLOSED");
        loan.setClosedAt(LocalDateTime.now());
        loanRepository.save(loan);
    }

    @Override
    public Page<LoanResponseDTO> getLoansByStatus(String status, Pageable pageable) {
        return loanRepository.findByStatus(status, pageable).map(loan -> LoanResponseDTO.builder()
                .loanId(loan.getLoanId())
                .loanType(loan.getLoanType())
                .sanctionedAmount(loan.getSanctionedAmount())
                .status(loan.getStatus())
                .build());
    }
}
