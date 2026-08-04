package com.banksphere.modules.customer.mapper;

import com.banksphere.modules.customer.dto.request.CustomerRegistrationDTO;
import com.banksphere.modules.customer.dto.response.CustomerResponseDTO;
import com.banksphere.modules.customer.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerResponseDTO toResponseDTO(Customer customer);
    Customer toEntity(CustomerRegistrationDTO dto);
}
