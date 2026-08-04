package com.banksphere.modules.fixeddeposit.mapper;

import com.banksphere.modules.fixeddeposit.dto.CreateFdRequestDTO;
import com.banksphere.modules.fixeddeposit.dto.FixedDepositResponseDTO;
import com.banksphere.modules.fixeddeposit.entity.FixedDeposit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FixedDepositMapper {
    FixedDeposit toEntity(CreateFdRequestDTO dto);
    FixedDepositResponseDTO toResponseDTO(FixedDeposit entity);
}
