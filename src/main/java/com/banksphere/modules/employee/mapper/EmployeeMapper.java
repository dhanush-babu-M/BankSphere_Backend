package com.banksphere.modules.employee.mapper;

import com.banksphere.modules.employee.dto.EmployeeCreateDTO;
import com.banksphere.modules.employee.dto.EmployeeResponseDTO;
import com.banksphere.modules.employee.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    Employee toEntity(EmployeeCreateDTO dto);

    @Mapping(target = "fullName", expression = "java(employee.getFirstName() + ' ' + employee.getLastName())")
    EmployeeResponseDTO toResponseDTO(Employee employee);
}
