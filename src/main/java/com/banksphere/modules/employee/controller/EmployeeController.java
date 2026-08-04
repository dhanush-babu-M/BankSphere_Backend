package com.banksphere.modules.employee.controller;

import com.banksphere.modules.employee.dto.EmployeeCreateDTO;
import com.banksphere.modules.employee.dto.EmployeeUpdateDTO;
import com.banksphere.modules.employee.entity.Branch;
import com.banksphere.modules.employee.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/")
    public ResponseEntity<?> createEmployee(@Valid @RequestBody EmployeeCreateDTO dto) {
        return ResponseEntity.ok(employeeService.createEmployee(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployee(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeService.getEmployee(id));
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllEmployees(Pageable pageable) {
        return ResponseEntity.ok(employeeService.getAllEmployees(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable UUID id, @RequestBody EmployeeUpdateDTO dto) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deactivateEmployee(@PathVariable UUID id) {
        employeeService.deactivateEmployee(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/department/{dept}")
    public ResponseEntity<?> getEmployeesByDepartment(@PathVariable String dept, Pageable pageable) {
        return ResponseEntity.ok(employeeService.getEmployeesByDepartment(dept, pageable));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<?> getEmployeesByBranch(@PathVariable UUID branchId, Pageable pageable) {
        return ResponseEntity.ok(employeeService.getEmployeesByBranch(branchId, pageable));
    }

    @PostMapping("/branches")
    public ResponseEntity<?> createBranch(@RequestBody Branch branch) {
        return ResponseEntity.ok(employeeService.createBranch(branch));
    }

    @GetMapping("/branches")
    public ResponseEntity<?> getAllBranches() {
        return ResponseEntity.ok(employeeService.getAllBranches());
    }
}
