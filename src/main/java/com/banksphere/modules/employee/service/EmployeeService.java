package com.banksphere.modules.employee.service;

import com.banksphere.modules.employee.dto.EmployeeCreateDTO;
import com.banksphere.modules.employee.dto.EmployeeResponseDTO;
import com.banksphere.modules.employee.dto.EmployeeUpdateDTO;
import com.banksphere.modules.employee.entity.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
    EmployeeResponseDTO createEmployee(EmployeeCreateDTO dto);
    EmployeeResponseDTO getEmployee(UUID id);
    EmployeeResponseDTO getEmployeeByEmployeeId(String employeeId);
    Page<EmployeeResponseDTO> getAllEmployees(Pageable pageable);
    EmployeeResponseDTO updateEmployee(UUID id, EmployeeUpdateDTO dto);
    void deactivateEmployee(UUID id);
    Page<EmployeeResponseDTO> getEmployeesByDepartment(String department, Pageable pageable);
    Page<EmployeeResponseDTO> getEmployeesByBranch(UUID branchId, Pageable pageable);
    Branch createBranch(Branch branch);
    Branch getBranch(UUID id);
    List<Branch> getAllBranches();
}
