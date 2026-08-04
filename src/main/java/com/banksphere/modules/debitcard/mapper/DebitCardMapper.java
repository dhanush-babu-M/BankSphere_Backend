package com.banksphere.modules.debitcard.mapper;

import com.banksphere.modules.debitcard.dto.request.IssueDebitCardRequestDTO;
import com.banksphere.modules.debitcard.dto.response.DebitCardResponseDTO;
import com.banksphere.modules.debitcard.entity.DebitCard;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DebitCardMapper {
    DebitCardResponseDTO toResponseDTO(DebitCard debitCard);
    DebitCard toEntity(IssueDebitCardRequestDTO dto);
}
