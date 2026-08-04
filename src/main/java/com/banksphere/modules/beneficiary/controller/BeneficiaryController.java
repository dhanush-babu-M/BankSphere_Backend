package com.banksphere.modules.beneficiary.controller;

import com.banksphere.modules.beneficiary.dto.request.AddBeneficiaryRequestDTO;
import com.banksphere.modules.beneficiary.dto.request.UpdateBeneficiaryRequestDTO;
import com.banksphere.modules.beneficiary.dto.response.BeneficiaryResponseDTO;
import com.banksphere.modules.beneficiary.service.BeneficiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/beneficiaries")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Beneficiary", description = "Beneficiary / Payee Management APIs")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    @PostMapping
    @Operation(summary = "Add a new beneficiary")
    public ResponseEntity<?> addBeneficiary(@Valid @RequestBody AddBeneficiaryRequestDTO request) {
        // TODO: implement
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get beneficiary by ID")
    public ResponseEntity<?> getBeneficiary(@PathVariable UUID id) {
        // TODO: implement
        return ResponseEntity.ok().build();
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get all beneficiaries for a customer")
    public ResponseEntity<?> getCustomerBeneficiaries(@PathVariable UUID customerId) {
        // TODO: implement
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update beneficiary")
    public ResponseEntity<?> updateBeneficiary(@PathVariable UUID id,
                                               @Valid @RequestBody UpdateBeneficiaryRequestDTO request) {
        // TODO: implement
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete beneficiary")
    public ResponseEntity<?> deleteBeneficiary(@PathVariable UUID id) {
        // TODO: implement
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/verify")
    @Operation(summary = "Verify beneficiary account")
    public ResponseEntity<?> verifyBeneficiary(@PathVariable UUID id) {
        // TODO: implement
        return ResponseEntity.ok().build();
    }
}
