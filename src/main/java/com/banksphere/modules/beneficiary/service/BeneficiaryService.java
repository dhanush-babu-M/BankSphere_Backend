package com.banksphere.modules.beneficiary.service;

import com.banksphere.modules.beneficiary.dto.request.AddBeneficiaryRequestDTO;
import com.banksphere.modules.beneficiary.dto.request.UpdateBeneficiaryRequestDTO;
import com.banksphere.modules.beneficiary.dto.response.BeneficiaryResponseDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface BeneficiaryService {
    BeneficiaryResponseDTO addBeneficiary(AddBeneficiaryRequestDTO request);
    BeneficiaryResponseDTO getBeneficiary(UUID id);
    List<BeneficiaryResponseDTO> getCustomerBeneficiaries(UUID customerId);
    BeneficiaryResponseDTO updateBeneficiary(UUID id, UpdateBeneficiaryRequestDTO request);
    void deleteBeneficiary(UUID id);
    BeneficiaryResponseDTO verifyBeneficiary(UUID id);
    boolean isTransferAllowed(UUID beneficiaryId, BigDecimal amount);
}
