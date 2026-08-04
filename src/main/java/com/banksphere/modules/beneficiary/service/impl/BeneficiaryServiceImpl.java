package com.banksphere.modules.beneficiary.service.impl;

import com.banksphere.core.exception.DuplicateResourceException;
import com.banksphere.core.exception.ResourceNotFoundException;
import com.banksphere.modules.beneficiary.dto.request.AddBeneficiaryRequestDTO;
import com.banksphere.modules.beneficiary.dto.response.BeneficiaryResponseDTO;
import com.banksphere.modules.beneficiary.dto.request.UpdateBeneficiaryRequestDTO;
import com.banksphere.modules.beneficiary.entity.Beneficiary;
import com.banksphere.modules.beneficiary.mapper.BeneficiaryMapper;
import com.banksphere.modules.beneficiary.repository.BeneficiaryRepository;
import com.banksphere.modules.beneficiary.service.BeneficiaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final BeneficiaryMapper beneficiaryMapper;

    @Override
    public BeneficiaryResponseDTO addBeneficiary(AddBeneficiaryRequestDTO request) {
        UUID ownerCustomerId = request.getOwnerCustomerId();
        log.info("Adding new beneficiary for customer: {}", ownerCustomerId);
        if (beneficiaryRepository.existsByOwnerCustomerIdAndAccountNumber(ownerCustomerId, request.getAccountNumber())) {
            throw new DuplicateResourceException("Beneficiary", "accountNumber", request.getAccountNumber());
        }

        Beneficiary beneficiary = Beneficiary.builder()
                .ownerCustomerId(ownerCustomerId)
                .beneficiaryName(request.getBeneficiaryName())
                .accountNumber(request.getAccountNumber())
                .ifscCode(request.getIfscCode())
                .bankName(request.getBankName())
                .nickname(request.getNickname())
                .accountType(request.getAccountType())
                .dailyTransferLimit(request.getDailyTransferLimit())
                .verified(false)
                .active(true)
                .build();

        beneficiary = beneficiaryRepository.save(beneficiary);
        
        BeneficiaryResponseDTO response = beneficiaryMapper.toResponseDTO(beneficiary);
        response.setAccountNumber(maskAccountNumber(response.getAccountNumber()));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public BeneficiaryResponseDTO getBeneficiary(UUID id) {
        Beneficiary beneficiary = beneficiaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary", "id", id));
        return beneficiaryMapper.toResponseDTO(beneficiary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BeneficiaryResponseDTO> getCustomerBeneficiaries(UUID customerId) {
        return beneficiaryRepository.findByOwnerCustomerIdAndActive(customerId, true)
                .stream()
                .map(beneficiaryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BeneficiaryResponseDTO updateBeneficiary(UUID id, UpdateBeneficiaryRequestDTO request) {
        Beneficiary beneficiary = beneficiaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary", "id", id));

        if (request.getNickname() != null) {
            beneficiary.setNickname(request.getNickname());
        }
        if (request.getActive() != null) {
            beneficiary.setActive(request.getActive());
        }
        if (request.getDailyTransferLimit() != null) {
            beneficiary.setDailyTransferLimit(request.getDailyTransferLimit());
        }

        beneficiary = beneficiaryRepository.save(beneficiary);
        return beneficiaryMapper.toResponseDTO(beneficiary);
    }

    @Override
    public void deleteBeneficiary(UUID id) {
        Beneficiary beneficiary = beneficiaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary", "id", id));
        beneficiary.setActive(false);
        beneficiaryRepository.save(beneficiary);
    }

    @Override
    public BeneficiaryResponseDTO verifyBeneficiary(UUID id) {
        Beneficiary beneficiary = beneficiaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary", "id", id));
        
        // In production: call penny-drop API first
        beneficiary.setVerified(true);
        beneficiary = beneficiaryRepository.save(beneficiary);
        return beneficiaryMapper.toResponseDTO(beneficiary);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTransferAllowed(UUID beneficiaryId, BigDecimal amount) {
        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary", "id", beneficiaryId));

        if (!beneficiary.isActive()) {
            return false;
        }

        if (beneficiary.getDailyTransferLimit() != null) {
            return amount.compareTo(beneficiary.getDailyTransferLimit()) <= 0;
        }

        return true;
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) return accountNumber;
        return "*".repeat(accountNumber.length() - 4) + accountNumber.substring(accountNumber.length() - 4);
    }
}
