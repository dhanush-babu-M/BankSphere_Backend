package com.banksphere.modules.creditcard.mapper;

import com.banksphere.modules.creditcard.dto.request.CreditCardApplicationDTO;
import com.banksphere.modules.creditcard.dto.response.CreditCardResponseDTO;
import com.banksphere.modules.creditcard.entity.CreditCard;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreditCardMapper {
    CreditCardResponseDTO toResponseDTO(CreditCard creditCard);
    CreditCard toEntity(CreditCardApplicationDTO dto);
}
