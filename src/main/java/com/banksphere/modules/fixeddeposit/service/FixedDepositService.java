package com.banksphere.modules.fixeddeposit.service;

import com.banksphere.modules.fixeddeposit.dto.CreateFdRequestDTO;
import com.banksphere.modules.fixeddeposit.dto.FdInterestCalculationDTO;
import com.banksphere.modules.fixeddeposit.dto.FixedDepositResponseDTO;
import com.banksphere.modules.fixeddeposit.dto.PrematureFdClosureDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface FixedDepositService {
    FixedDepositResponseDTO createFd(CreateFdRequestDTO dto);
    FixedDepositResponseDTO getFd(UUID id);
    FixedDepositResponseDTO getFdByNumber(String fdNumber);
    Page<FixedDepositResponseDTO> getCustomerFds(UUID customerId, Pageable pageable);
    FdInterestCalculationDTO calculateInterest(BigDecimal principal, BigDecimal rate, int months, String fdType);
    FixedDepositResponseDTO prematureClose(PrematureFdClosureDTO dto);
    void processMaturity(UUID fdId);
    FixedDepositResponseDTO renewFd(UUID fdId);
    Page<FixedDepositResponseDTO> getAllActiveFds(Pageable pageable);
}
