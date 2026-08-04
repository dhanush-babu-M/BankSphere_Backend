package com.banksphere.modules.account.mapper;

import com.banksphere.modules.account.dto.request.CreateAccountRequestDTO;
import com.banksphere.modules.account.dto.response.AccountResponseDTO;
import com.banksphere.modules.account.dto.response.AccountSummaryDTO;
import com.banksphere.modules.account.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountResponseDTO toResponseDTO(Account account);
    AccountSummaryDTO toSummaryDTO(Account account);
    Account toEntity(CreateAccountRequestDTO requestDTO);
}
