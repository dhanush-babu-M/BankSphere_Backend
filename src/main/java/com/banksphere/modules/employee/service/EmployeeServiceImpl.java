package com.banksphere.modules.employee.service;

import com.banksphere.modules.employee.dto.EmployeeCreateDTO;
import com.banksphere.modules.employee.dto.EmployeeResponseDTO;
import com.banksphere.modules.employee.dto.EmployeeUpdateDTO;
import com.banksphere.modules.employee.entity.Branch;
import com.banksphere.modules.employee.entity.Employee;
import com.banksphere.modules.employee.mapper.EmployeeMapper;
import com.banksphere.modules.employee.repository.BranchRepository;
import com.banksphere.modules.employee.repository.EmployeeRepository;
import com.banksphere.modules.auth.service.AuthService;
import com.banksphere.core.exception.DuplicateResourceException;
import com.banksphere.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final BranchRepository branchRepository;
    private final EmployeeMapper employeeMapper;
    private final AuthService authService;

    public EmployeeResponseDTO createEmployee(EmployeeCreateDTO request) {
        employeeRepository.findByEmail(request.getEmail()).ifPresent(e -> {
            throw new DuplicateResourceException("Employee", "email", request.getEmail());
        });

        var user = authService.registerUser(request.getEmail(), request.getEmail(), request.getPassword(), List.of("ROLE_EMPLOYEE"));
        
        String employeeId = "EMP" + String.format("%06d", new Random().nextInt(999999));
        
        Employee employee = new Employee();
        employee.setUserId(user.getId());
        employee.setEmployeeId(employeeId);
        employee.setActive(true);
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setDepartment(request.getDepartment());
        employee.setDesignation(request.getDesignation());
        employee.setBranchId(request.getBranchId());
        employee.setSalary(request.getSalary());

        employee = employeeRepository.save(employee);
        return employeeMapper.toResponseDTO(employee);
    }

    public EmployeeResponseDTO getEmployee(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id.toString()));
        return employeeMapper.toResponseDTO(employee);
    }

    public EmployeeResponseDTO getEmployeeByEmployeeId(String eid) {
        Employee employee = employeeRepository.findByEmployeeId(eid)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "employeeId", eid));
        return employeeMapper.toResponseDTO(employee);
    }

    public Page<EmployeeResponseDTO> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable).map(employeeMapper::toResponseDTO);
    }

    public EmployeeResponseDTO updateEmployee(UUID id, EmployeeUpdateDTO request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id.toString()));

        if (request.getFirstName() != null) employee.setFirstName(request.getFirstName());
        if (request.getLastName() != null) employee.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) employee.setPhoneNumber(request.getPhoneNumber());
        if (request.getDepartment() != null) employee.setDepartment(request.getDepartment());
        if (request.getDesignation() != null) employee.setDesignation(request.getDesignation());
        if (request.getBranchId() != null) employee.setBranchId(request.getBranchId());
        if (request.getSalary() != null) employee.setSalary(request.getSalary());
        if (request.getActive() != null) employee.setActive(request.getActive());

        employee = employeeRepository.save(employee);
        return employeeMapper.toResponseDTO(employee);
    }

    public void deactivateEmployee(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id.toString()));
        employee.setActive(false);
        employeeRepository.save(employee);
    }

    public Page<EmployeeResponseDTO> getEmployeesByDepartment(String dept, Pageable pageable) {
        return employeeRepository.findByDepartment(dept, pageable).map(employeeMapper::toResponseDTO);
    }

    public Page<EmployeeResponseDTO> getEmployeesByBranch(UUID branchId, Pageable pageable) {
        return employeeRepository.findByBranchId(branchId, pageable).map(employeeMapper::toResponseDTO);
    }

    public Branch createBranch(Branch branch) {
        return branchRepository.save(branch);
    }

    public Branch getBranch(UUID id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", id.toString()));
    }

    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }
}

