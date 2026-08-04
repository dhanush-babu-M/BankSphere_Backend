package com.banksphere.modules.debitcard.service;

import com.banksphere.modules.debitcard.dto.request.DebitCardLimitUpdateDTO;
import com.banksphere.modules.debitcard.dto.request.DebitCardPinChangeDTO;
import com.banksphere.modules.debitcard.dto.request.IssueDebitCardRequestDTO;
import com.banksphere.modules.debitcard.dto.response.DebitCardResponseDTO;

import java.util.List;
import java.util.UUID;

public interface DebitCardService {
    DebitCardResponseDTO issueDebitCard(IssueDebitCardRequestDTO request);
    DebitCardResponseDTO getDebitCard(UUID id);
    List<DebitCardResponseDTO> getAccountDebitCards(UUID accountId);
    void changePin(DebitCardPinChangeDTO request);
    DebitCardResponseDTO updateLimits(DebitCardLimitUpdateDTO request);
    void blockCard(UUID id, String reason);
    void hotlistCard(UUID id, String reason);
    void enableInternational(UUID id, boolean enable);
    void enableContactless(UUID id, boolean enable);
    DebitCardResponseDTO renewCard(UUID id);
}
