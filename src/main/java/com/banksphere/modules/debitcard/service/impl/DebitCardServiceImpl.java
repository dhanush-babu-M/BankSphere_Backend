package com.banksphere.modules.debitcard.service.impl;

import com.banksphere.core.exception.ResourceNotFoundException;
import com.banksphere.core.exception.UnauthorizedAccessException;
import com.banksphere.modules.debitcard.dto.request.*;
import com.banksphere.modules.debitcard.dto.response.*;
import com.banksphere.modules.debitcard.entity.DebitCard;
import com.banksphere.modules.debitcard.mapper.DebitCardMapper;
import com.banksphere.modules.debitcard.repository.DebitCardRepository;
import com.banksphere.modules.debitcard.service.DebitCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DebitCardServiceImpl implements DebitCardService {

    private final DebitCardRepository debitCardRepository;
    private final DebitCardMapper debitCardMapper;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom random = new SecureRandom();

    @Override
    public DebitCardResponseDTO issueDebitCard(IssueDebitCardRequestDTO request) {
        StringBuilder cardNumber = new StringBuilder("6");
        for (int i = 0; i < 15; i++) {
            cardNumber.append(random.nextInt(10));
        }

        String last4 = cardNumber.substring(12);
        String maskedCardNumber = "****-****-****-" + last4;
        String cvv = String.format("%03d", random.nextInt(1000));
        LocalDate expiryDate = LocalDate.now().plusYears(3);
        String pinHash = passwordEncoder.encode("0000"); // default PIN

        DebitCard card = new DebitCard();
        card.setAccountId(request.getAccountId());
        card.setCustomerId(request.getCustomerId());
        card.setCardHolderName(request.getCardHolderName());
        card.setCardNumber(cardNumber.toString());
        card.setMaskedCardNumber(maskedCardNumber);
        card.setCvv(cvv); // TODO: encrypt
        card.setExpiryDate(expiryDate);
        card.setPinHash(pinHash);
        card.setStatus("ACTIVE");

        card.setDailyAtmLimit(request.getDailyAtmLimit() != null ? request.getDailyAtmLimit() : BigDecimal.valueOf(10000));
        card.setDailyPosLimit(request.getDailyPosLimit() != null ? request.getDailyPosLimit() : BigDecimal.valueOf(50000));
        card.setDailyOnlineLimit(request.getDailyOnlineLimit() != null ? request.getDailyOnlineLimit() : BigDecimal.valueOf(25000));
        
        card.setInternationalEnabled(false);
        card.setContactlessEnabled(true);

        card = debitCardRepository.save(card);
        return debitCardMapper.toResponseDTO(card);
    }

    @Override
    public DebitCardResponseDTO getDebitCard(UUID id) {
        return debitCardRepository.findById(id)
                .map(debitCardMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("DebitCard", "id", id));
    }

    @Override
    public List<DebitCardResponseDTO> getAccountDebitCards(UUID accountId) {
        return debitCardRepository.findByAccountId(accountId).stream()
                .map(debitCardMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void changePin(DebitCardPinChangeDTO request) {
        DebitCard card = debitCardRepository.findById(request.getCardId())
                .orElseThrow(() -> new ResourceNotFoundException("DebitCard", "id", request.getCardId()));

        if (!"ACTIVE".equals(card.getStatus())) {
            throw new IllegalArgumentException("Card is not active");
        }

        if (!passwordEncoder.matches(request.getCurrentPin(), card.getPinHash())) {
            throw new UnauthorizedAccessException("Current PIN is incorrect");
        }

        if (!request.getNewPin().equals(request.getConfirmPin())) {
            throw new IllegalArgumentException("PINs do not match");
        }

        card.setPinHash(passwordEncoder.encode(request.getNewPin()));
        debitCardRepository.save(card);
        log.info("PIN changed for card: {}", request.getCardId());
    }

    @Override
    public DebitCardResponseDTO updateLimits(DebitCardLimitUpdateDTO request) {
        DebitCard card = debitCardRepository.findById(request.getCardId())
                .orElseThrow(() -> new ResourceNotFoundException("DebitCard", "id", request.getCardId()));

        if (request.getDailyAtmLimit() != null) card.setDailyAtmLimit(request.getDailyAtmLimit());
        if (request.getDailyPosLimit() != null) card.setDailyPosLimit(request.getDailyPosLimit());
        if (request.getDailyOnlineLimit() != null) card.setDailyOnlineLimit(request.getDailyOnlineLimit());

        card = debitCardRepository.save(card);
        return debitCardMapper.toResponseDTO(card);
    }

    @Override
    public void blockCard(UUID id, String reason) {
        DebitCard card = debitCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DebitCard", "id", id));
        
        if ("BLOCKED".equals(card.getStatus())) {
            throw new IllegalArgumentException("Card is already blocked");
        }

        card.setStatus("BLOCKED");
        debitCardRepository.save(card);
        log.info("Debit card blocked: {} reason: {}", id, reason);
    }

    @Override
    public void hotlistCard(UUID id, String reason) {
        DebitCard card = debitCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DebitCard", "id", id));

        card.setStatus("HOT_LISTED");
        debitCardRepository.save(card);
        log.info("Card hotlisted: {} reason: {}", id, reason);
    }

    @Override
    public void enableInternational(UUID id, boolean enabled) {
        DebitCard card = debitCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DebitCard", "id", id));
        card.setInternationalEnabled(enabled);
        debitCardRepository.save(card);
    }

    @Override
    public void enableContactless(UUID id, boolean enabled) {
        DebitCard card = debitCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DebitCard", "id", id));
        card.setContactlessEnabled(enabled);
        debitCardRepository.save(card);
    }

    @Override
    public DebitCardResponseDTO renewCard(UUID id) {
        DebitCard oldCard = debitCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DebitCard", "id", id));
        
        oldCard.setStatus("EXPIRED");
        debitCardRepository.save(oldCard);

        StringBuilder cardNumber = new StringBuilder("6");
        for (int i = 0; i < 15; i++) {
            cardNumber.append(random.nextInt(10));
        }

        DebitCard newCard = new DebitCard();
        newCard.setAccountId(oldCard.getAccountId());
        newCard.setCustomerId(oldCard.getCustomerId());
        newCard.setCardHolderName(oldCard.getCardHolderName());
        newCard.setCardNumber(cardNumber.toString());
        newCard.setMaskedCardNumber("****-****-****-" + cardNumber.substring(12));
        newCard.setCvv(String.format("%03d", random.nextInt(1000)));
        newCard.setExpiryDate(LocalDate.now().plusYears(3));
        newCard.setPinHash(oldCard.getPinHash());
        newCard.setStatus("ACTIVE");
        newCard.setDailyAtmLimit(oldCard.getDailyAtmLimit());
        newCard.setDailyPosLimit(oldCard.getDailyPosLimit());
        newCard.setDailyOnlineLimit(oldCard.getDailyOnlineLimit());
        newCard.setInternationalEnabled(oldCard.isInternationalEnabled());
        newCard.setContactlessEnabled(oldCard.isContactlessEnabled());

        newCard = debitCardRepository.save(newCard);
        return debitCardMapper.toResponseDTO(newCard);
    }
}

