package com.banksphere.modules.beneficiary.mapper;

import com.banksphere.modules.beneficiary.dto.request.AddBeneficiaryRequestDTO;
import com.banksphere.modules.beneficiary.dto.response.BeneficiaryResponseDTO;
import com.banksphere.modules.beneficiary.entity.Beneficiary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BeneficiaryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "verified", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "addedAt", ignore = true)
    @Mapping(target = "lastUsedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Beneficiary toEntity(AddBeneficiaryRequestDTO dto);

    BeneficiaryResponseDTO toResponseDTO(Beneficiary beneficiary);
}
