package com.banksphere.modules.admin.service.impl;

import com.banksphere.modules.admin.dto.SystemHealthDTO;
import com.banksphere.modules.admin.dto.SystemParameterDTO;
import com.banksphere.modules.admin.dto.PendingApprovalDTO;
import com.banksphere.modules.admin.dto.MakerCheckerApprovalDTO;
import com.banksphere.modules.admin.entity.SystemConfiguration;
import com.banksphere.modules.admin.entity.PendingOperation;
import com.banksphere.modules.admin.mapper.AdminMapper;
import com.banksphere.modules.admin.repository.SystemConfigurationRepository;
import com.banksphere.modules.admin.repository.PendingOperationRepository;
import com.banksphere.core.exception.ResourceNotFoundException;
import com.banksphere.modules.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final SystemConfigurationRepository systemConfigurationRepository;
    private final PendingOperationRepository pendingOperationRepository;
    private final AdminMapper adminMapper;
    private final DataSource dataSource;

    public SystemHealthDTO getSystemHealth() {
        String dbStatus = "DOWN";
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                dbStatus = "UP";
            }
        } catch (Exception e) {
            log.error("Error checking DB connection", e);
        }

        SystemHealthDTO health = new SystemHealthDTO();
        health.setStatus("UP".equals(dbStatus) ? "HEALTHY" : "DEGRADED");
        health.setUptime(ManagementFactory.getRuntimeMXBean().getUptime() / 1000 + " seconds");
        health.setDbStatus(dbStatus);
        health.setCacheStatus("UP");
        health.setSchedulerStatus("UP");
        health.setActiveUsers(0);
        health.setTotalTransactionsToday(0);
        health.setSystemVersion("1.0.0");
        health.setJavaVersion(System.getProperty("java.version"));
        health.setTimestamp(LocalDateTime.now());
        
        return health;
    }

    public String getConfigValue(String key) {
        return systemConfigurationRepository.findByConfigKey(key)
                .map(SystemConfiguration::getConfigValue)
                .orElseThrow(() -> new ResourceNotFoundException("SystemConfiguration", "key", key));
    }

    public SystemConfiguration updateConfig(SystemParameterDTO dto) {
        SystemConfiguration config = systemConfigurationRepository.findByConfigKey(dto.getConfigKey())
                .orElse(new SystemConfiguration());
        
        config.setConfigKey(dto.getConfigKey());
        config.setConfigValue(dto.getConfigValue());
        config.setConfigType(dto.getConfigType());
        config.setDescription(dto.getDescription());
        config.setModule(dto.getModule());
        config.setLastModifiedBy("ADMIN");
        
        return systemConfigurationRepository.save(config);
    }

    public List<SystemConfiguration> getAllConfigs(String module) {
        if (module != null && !module.isBlank()) {
            return systemConfigurationRepository.findByModule(module);
        } else {
            return systemConfigurationRepository.findAll();
        }
    }

    public PendingOperation createPendingOperation(String type, String entityName, String entityId, Object data, String requestedBy) {
        PendingOperation op = new PendingOperation();
        op.setOperationType(type);
        op.setEntityName(entityName);
        op.setEntityId(entityId);
        op.setRequestData(data.toString());
        op.setRequestedBy(requestedBy);
        op.setRequestedAt(LocalDateTime.now());
        op.setStatus("PENDING");
        op.setModule(entityName);
        
        return pendingOperationRepository.save(op);
    }

    public PendingApprovalDTO approvePendingOperation(MakerCheckerApprovalDTO dto, String reviewerUsername) {
        PendingOperation operation = pendingOperationRepository.findById(dto.getOperationId())
                .orElseThrow(() -> new ResourceNotFoundException("PendingOperation", "id", dto.getOperationId().toString()));
        
        operation.setStatus(dto.isApproved() ? "APPROVED" : "REJECTED");
        operation.setReviewedBy(reviewerUsername);
        operation.setReviewedAt(LocalDateTime.now());
        operation.setComments(dto.getComments());
        
        operation = pendingOperationRepository.save(operation);
        
        PendingApprovalDTO approvalDTO = new PendingApprovalDTO();
        approvalDTO.setId(operation.getId());
        approvalDTO.setOperationType(operation.getOperationType());
        approvalDTO.setEntityName(operation.getEntityName());
        approvalDTO.setRequestedBy(operation.getRequestedBy());
        approvalDTO.setRequestedAt(operation.getRequestedAt());
        approvalDTO.setStatus(operation.getStatus());
        approvalDTO.setModule(operation.getModule());
        
        return approvalDTO;
    }

    public Page<PendingApprovalDTO> getPendingOperations(String status, Pageable pageable) {
        return pendingOperationRepository.findByStatus(status, pageable)
                .map(op -> {
                    PendingApprovalDTO dto = new PendingApprovalDTO();
                    dto.setId(op.getId());
                    dto.setOperationType(op.getOperationType());
                    dto.setEntityName(op.getEntityName());
                    dto.setRequestedBy(op.getRequestedBy());
                    dto.setRequestedAt(op.getRequestedAt());
                    dto.setStatus(op.getStatus());
                    dto.setModule(op.getModule());
                    return dto;
                });
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalConfigurations", systemConfigurationRepository.count());
        stats.put("pendingApprovals", pendingOperationRepository.countByStatus("PENDING"));
        stats.put("approvedOperations", pendingOperationRepository.countByStatus("APPROVED"));
        stats.put("rejectedOperations", pendingOperationRepository.countByStatus("REJECTED"));
        stats.put("timestamp", LocalDateTime.now().toString());
        
        return stats;
    }
}

