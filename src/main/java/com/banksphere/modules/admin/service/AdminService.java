package com.banksphere.modules.admin.service;

import com.banksphere.modules.admin.dto.MakerCheckerApprovalDTO;
import com.banksphere.modules.admin.dto.PendingApprovalDTO;
import com.banksphere.modules.admin.dto.SystemHealthDTO;
import com.banksphere.modules.admin.dto.SystemParameterDTO;
import com.banksphere.modules.admin.entity.PendingOperation;
import com.banksphere.modules.admin.entity.SystemConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface AdminService {
    SystemHealthDTO getSystemHealth();
    String getConfigValue(String key);
    SystemConfiguration updateConfig(SystemParameterDTO request);
    List<SystemConfiguration> getAllConfigs(String module);
    PendingOperation createPendingOperation(String type, String entityName, String entityId, Object data, String requestedBy);
    PendingApprovalDTO approvePendingOperation(MakerCheckerApprovalDTO dto, String reviewerUsername);
    Page<PendingApprovalDTO> getPendingOperations(String status, Pageable pageable);
    Map<String, Object> getDashboardStats();
}
