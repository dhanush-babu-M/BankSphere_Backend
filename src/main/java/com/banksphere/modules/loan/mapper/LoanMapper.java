package com.banksphere.modules.loan.mapper;

import com.banksphere.modules.loan.dto.LoanApplicationRequestDTO;
import com.banksphere.modules.loan.dto.LoanResponseDTO;
import com.banksphere.modules.loan.entity.Loan;
import com.banksphere.modules.loan.entity.LoanApplication;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanMapper {
    LoanApplication toApplicationEntity(LoanApplicationRequestDTO dto);
    LoanResponseDTO toResponseDTO(Loan entity);
    LoanResponseDTO toResponseDTO(LoanApplication entity);
}
