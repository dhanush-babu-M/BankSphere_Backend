package com.banksphere.modules.admin.controller;

import com.banksphere.modules.admin.dto.MakerCheckerApprovalDTO;
import com.banksphere.modules.admin.dto.PendingApprovalDTO;
import com.banksphere.modules.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/maker-checker")
@RequiredArgsConstructor
@Tag(name = "Maker Checker API")
@Slf4j
public class MakerCheckerController {

    private final AdminService adminService;

    @GetMapping("/pending")
    @Operation(summary = "Get pending operations")
    public ResponseEntity<Page<PendingApprovalDTO>> getPendingOperations(@RequestParam(defaultValue = "PENDING") String status, Pageable pageable) {
        return ResponseEntity.ok(adminService.getPendingOperations(status, pageable));
    }

    @PostMapping("/approve")
    @Operation(summary = "Approve pending operation")
    public ResponseEntity<PendingApprovalDTO> approvePendingOperation(@RequestBody MakerCheckerApprovalDTO dto) {
        return ResponseEntity.ok(adminService.approvePendingOperation(dto, "admin"));
    }

    @GetMapping("/history")
    @Operation(summary = "Get approval history")
    public ResponseEntity<Page<PendingApprovalDTO>> getHistory(Pageable pageable) {
        return ResponseEntity.ok(adminService.getPendingOperations("APPROVED", pageable)); // simplified
    }
}
