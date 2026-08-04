package com.banksphere.modules.admin.mapper;

import com.banksphere.modules.admin.dto.PendingApprovalDTO;
import com.banksphere.modules.admin.dto.SystemHealthDTO;
import com.banksphere.modules.admin.dto.SystemParameterDTO;
import com.banksphere.modules.admin.entity.PendingOperation;
import com.banksphere.modules.admin.entity.SystemConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminMapper {
    SystemHealthDTO toSystemHealthDTO(Object source);
    SystemParameterDTO toConfigDTO(SystemConfiguration configuration);
    PendingApprovalDTO toPendingApprovalDTO(PendingOperation pendingOperation);
}
