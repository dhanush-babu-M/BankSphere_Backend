package com.banksphere.modules.admin.controller;

import com.banksphere.modules.admin.dto.SystemHealthDTO;
import com.banksphere.modules.admin.dto.SystemParameterDTO;
import com.banksphere.modules.admin.entity.SystemConfiguration;
import com.banksphere.modules.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/system")
@RequiredArgsConstructor
@Tag(name = "Admin System API")
@Slf4j
public class AdminSystemController {

    private final AdminService adminService;

    @GetMapping("/health")
    @Operation(summary = "Get system health")
    public ResponseEntity<SystemHealthDTO> getSystemHealth() {
        return ResponseEntity.ok(adminService.getSystemHealth());
    }

    @GetMapping("/config")
    @Operation(summary = "Get config value")
    public ResponseEntity<String> getConfigValue(@RequestParam String key) {
        return ResponseEntity.ok(adminService.getConfigValue(key));
    }

    @PutMapping("/config")
    @Operation(summary = "Update system configuration")
    public ResponseEntity<SystemConfiguration> updateConfig(@RequestBody SystemParameterDTO request) {
        return ResponseEntity.ok(adminService.updateConfig(request));
    }

    @GetMapping("/config/{module}")
    @Operation(summary = "Get configs by module")
    public ResponseEntity<List<SystemConfiguration>> getAllConfigs(@PathVariable String module) {
        return ResponseEntity.ok(adminService.getAllConfigs(module));
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }
}
