package com.banksphere.modules.fixeddeposit.service;

import com.banksphere.core.exception.InsufficientFundsException;
import com.banksphere.core.exception.ResourceNotFoundException;
import com.banksphere.modules.account.repository.AccountRepository;
import com.banksphere.modules.fixeddeposit.dto.*;
import com.banksphere.modules.fixeddeposit.entity.FixedDeposit;
import com.banksphere.modules.fixeddeposit.mapper.FixedDepositMapper;
import com.banksphere.modules.fixeddeposit.repository.FixedDepositRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FixedDepositServiceImpl implements FixedDepositService {

    private final FixedDepositRepository fixedDepositRepository;
    private final FixedDepositMapper fixedDepositMapper;
    private final AccountRepository accountRepository;

    @Override
    public FixedDepositResponseDTO createFd(CreateFdRequestDTO request) {
        var account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.getAccountId()));

        if (!"ACTIVE".equals(account.getStatus())) {
            throw new IllegalArgumentException("Account is not active");
        }

        if (account.getAvailableBalance().compareTo(request.getPrincipalAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds in account for FD creation");
        }

        var interestCalc = calculateInterest(
                request.getPrincipalAmount(),
                request.getInterestRate(),
                request.getTenureMonths(),
                request.getFdType()
        );

        String fdNumber = "FD" + String.format("%010d", System.currentTimeMillis() % 10000000000L);

        account.setBalance(account.getBalance().subtract(request.getPrincipalAmount()));
        account.setAvailableBalance(account.getAvailableBalance().subtract(request.getPrincipalAmount()));
        accountRepository.save(account);

        LocalDate maturityDate = LocalDate.now().plusMonths(request.getTenureMonths());

        FixedDeposit fd = new FixedDeposit();
        fd.setFdNumber(fdNumber);
        fd.setCustomerId(request.getCustomerId());
        fd.setAccountId(request.getAccountId());
        fd.setPrincipalAmount(request.getPrincipalAmount());
        fd.setInterestRate(request.getInterestRate());
        fd.setTenureMonths(request.getTenureMonths());
        fd.setFdType(request.getFdType());
        fd.setAutoRenew(request.isAutoRenew());
        fd.setStartDate(LocalDate.now());
        fd.setMaturityDate(maturityDate);
        fd.setStatus("ACTIVE");
        fd.setMaturityAmount(interestCalc.getMaturityAmount());

        fd = fixedDepositRepository.save(fd);

        log.info("FD created: {}, amount: {}", fdNumber, request.getPrincipalAmount());

        return fixedDepositMapper.toResponseDTO(fd);
    }

    @Override
    public FixedDepositResponseDTO getFd(UUID id) {
        return fixedDepositRepository.findById(id)
                .map(fixedDepositMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("FixedDeposit", "id", id));
    }

    @Override
    public FixedDepositResponseDTO getFdByNumber(String fdNumber) {
        return fixedDepositRepository.findByFdNumber(fdNumber)
                .map(fixedDepositMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("FixedDeposit", "fdNumber", fdNumber));
    }

    @Override
    public Page<FixedDepositResponseDTO> getCustomerFds(UUID customerId, Pageable pageable) {
        return fixedDepositRepository.findByCustomerId(customerId, pageable)
                .map(fixedDepositMapper::toResponseDTO);
    }

    @Override
    public FdInterestCalculationDTO calculateInterest(BigDecimal principal, BigDecimal rate, int months, String fdType) {
        BigDecimal years = BigDecimal.valueOf(months).divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        BigDecimal annualRate = rate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

        BigDecimal maturity;
        if ("CUMULATIVE".equalsIgnoreCase(fdType)) {
            BigDecimal quarterlyRate = annualRate.divide(BigDecimal.valueOf(4), 10, RoundingMode.HALF_UP);
            int quarters = months / 3 + (months % 3 > 0 ? 1 : 0);
            maturity = principal.multiply(BigDecimal.ONE.add(quarterlyRate).pow(quarters));
            maturity = maturity.setScale(2, RoundingMode.HALF_UP);
        } else {
            BigDecimal interest = principal.multiply(annualRate).multiply(years);
            maturity = principal.add(interest).setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal totalInterest = maturity.subtract(principal);

        FdInterestCalculationDTO dto = new FdInterestCalculationDTO();
        dto.setPrincipalAmount(principal);
        dto.setInterestRate(rate);
        dto.setTenureMonths(months);
        dto.setFdType(fdType);
        dto.setMaturityAmount(maturity);
        dto.setTotalInterest(totalInterest);
        dto.setEffectiveAnnualReturn(annualRate.multiply(BigDecimal.valueOf(100)));

        return dto;
    }

    @Override
    public FixedDepositResponseDTO prematureClose(PrematureFdClosureDTO request) {
        FixedDeposit fd = fixedDepositRepository.findById(request.getFdId())
                .orElseThrow(() -> new ResourceNotFoundException("FixedDeposit", "id", request.getFdId()));

        if (!"ACTIVE".equals(fd.getStatus())) {
            throw new IllegalArgumentException("FixedDeposit is not active");
        }

        BigDecimal effectiveRate = fd.getInterestRate().subtract(BigDecimal.valueOf(0.5));
        
        BigDecimal yearsElapsed = BigDecimal.ONE; // Simplified
        BigDecimal interest = fd.getPrincipalAmount().multiply(effectiveRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)).multiply(yearsElapsed);
        BigDecimal actualPayout = fd.getPrincipalAmount().add(interest);
        BigDecimal penaltyAmount = fd.getPrincipalAmount().multiply(BigDecimal.valueOf(0.005));
        
        actualPayout = actualPayout.subtract(penaltyAmount);

        fd.setStatus("PREMATURE_CLOSED");
        fd.setPrematureClosurePenalty(penaltyAmount);
        fd.setActualMaturityAmount(actualPayout);
        fd.setClosedAt(LocalDateTime.now());
        fd.setClosureReason(request.getReason());

        fixedDepositRepository.save(fd);

        UUID targetId = request.getTargetAccountId() != null ? request.getTargetAccountId() : fd.getAccountId();
        var account = accountRepository.findById(targetId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", targetId));

        account.setBalance(account.getBalance().add(actualPayout));
        account.setAvailableBalance(account.getAvailableBalance().add(actualPayout));
        accountRepository.save(account);

        return fixedDepositMapper.toResponseDTO(fd);
    }

    @Override
    public void processMaturity(UUID fdId) {
        FixedDeposit fd = fixedDepositRepository.findById(fdId)
                .orElseThrow(() -> new ResourceNotFoundException("FixedDeposit", "id", fdId));

        if (!"ACTIVE".equals(fd.getStatus()) || fd.getMaturityDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("FD cannot be processed for maturity");
        }

        var account = accountRepository.findById(fd.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", fd.getAccountId()));

        account.setBalance(account.getBalance().add(fd.getMaturityAmount()));
        account.setAvailableBalance(account.getAvailableBalance().add(fd.getMaturityAmount()));
        accountRepository.save(account);

        fd.setStatus("MATURED");
        fd.setActualMaturityAmount(fd.getMaturityAmount());
        fd.setClosedAt(LocalDateTime.now());
        fixedDepositRepository.save(fd);

        log.info("FD matured: {}", fdId);

        if (fd.isAutoRenew()) {
            CreateFdRequestDTO req = new CreateFdRequestDTO();
            req.setAccountId(fd.getAccountId());
            req.setCustomerId(fd.getCustomerId());
            req.setPrincipalAmount(fd.getPrincipalAmount());
            req.setInterestRate(fd.getInterestRate());
            req.setTenureMonths(fd.getTenureMonths());
            req.setFdType(fd.getFdType());
            req.setAutoRenew(true);
            createFd(req);
        }
    }

    @Override
    public FixedDepositResponseDTO renewFd(UUID fdId) {
        FixedDeposit fd = fixedDepositRepository.findById(fdId)
                .orElseThrow(() -> new ResourceNotFoundException("FixedDeposit", "id", fdId));

        if (!"ACTIVE".equals(fd.getStatus()) && !"MATURED".equals(fd.getStatus())) {
            throw new IllegalArgumentException("FD must be ACTIVE or MATURED to renew");
        }

        CreateFdRequestDTO req = new CreateFdRequestDTO();
        req.setAccountId(fd.getAccountId());
        req.setCustomerId(fd.getCustomerId());
        req.setPrincipalAmount(fd.getPrincipalAmount());
        req.setInterestRate(fd.getInterestRate());
        req.setTenureMonths(fd.getTenureMonths());
        req.setFdType(fd.getFdType());
        req.setAutoRenew(fd.isAutoRenew());

        fd.setStatus("MATURED");
        fd.setClosedAt(LocalDateTime.now());
        fixedDepositRepository.save(fd);

        return createFd(req);
    }

    @Override
    public Page<FixedDepositResponseDTO> getAllActiveFds(Pageable pageable) {
        Page<FixedDeposit> page = fixedDepositRepository.findAll(pageable);
        List<FixedDepositResponseDTO> activeFds = page.getContent().stream()
                .filter(fd -> "ACTIVE".equals(fd.getStatus()))
                .map(fixedDepositMapper::toResponseDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(activeFds, pageable, page.getTotalElements());
    }
}

