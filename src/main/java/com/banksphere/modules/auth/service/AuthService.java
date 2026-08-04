package com.banksphere.modules.auth.service;

import com.banksphere.modules.auth.dto.request.LoginRequestDTO;
import com.banksphere.modules.auth.dto.request.MfaVerificationDTO;
import com.banksphere.modules.auth.dto.request.PasswordResetRequestDTO;
import com.banksphere.modules.auth.dto.request.RefreshTokenRequestDTO;
import com.banksphere.modules.auth.dto.response.AuthTokenResponseDTO;
import com.banksphere.modules.auth.entity.User;

import java.util.List;

public interface AuthService {
    AuthTokenResponseDTO login(LoginRequestDTO request);
    AuthTokenResponseDTO refreshToken(RefreshTokenRequestDTO request);
    void logout(String refreshToken);
    AuthTokenResponseDTO verifyMfa(MfaVerificationDTO request);
    void initiatePasswordReset(String email);
    void resetPassword(PasswordResetRequestDTO request);
    void changePassword(String username, String oldPassword, String newPassword);
    User registerUser(String username, String email, String password, List<String> roles);
}
