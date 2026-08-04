package com.banksphere.modules.transaction.mapper;

import com.banksphere.modules.transaction.dto.TransactionResponseDTO;
import com.banksphere.modules.transaction.entity.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionResponseDTO toResponseDTO(Transaction transaction);
}
