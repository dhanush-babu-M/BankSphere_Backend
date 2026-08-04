package com.banksphere.modules.auth.mapper;

import com.banksphere.modules.auth.dto.response.AuthTokenResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    AuthTokenResponseDTO toAuthTokenResponse(Object source);
}
