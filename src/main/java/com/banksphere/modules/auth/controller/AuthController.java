package com.banksphere.modules.auth.controller;

import com.banksphere.modules.auth.dto.request.LoginRequestDTO;
import com.banksphere.modules.auth.dto.request.MfaVerificationDTO;
import com.banksphere.modules.auth.dto.request.PasswordResetRequestDTO;
import com.banksphere.modules.auth.dto.request.RefreshTokenRequestDTO;
import com.banksphere.modules.auth.dto.response.AuthTokenResponseDTO;
import com.banksphere.modules.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication API")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<AuthTokenResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token")
    public ResponseEntity<AuthTokenResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequestDTO request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mfa/verify")
    @Operation(summary = "Verify MFA")
    public ResponseEntity<AuthTokenResponseDTO> verifyMfa(@RequestBody MfaVerificationDTO request) {
        return ResponseEntity.ok(authService.verifyMfa(request));
    }

    @PostMapping("/password/reset-request")
    @Operation(summary = "Request password reset")
    public ResponseEntity<Void> initiatePasswordReset(@RequestParam String email) {
        authService.initiatePasswordReset(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/reset")
    @Operation(summary = "Reset password")
    public ResponseEntity<Void> resetPassword(@RequestBody PasswordResetRequestDTO request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/password/change")
    @Operation(summary = "Change password")
    public ResponseEntity<Void> changePassword(@RequestParam String username, @RequestParam String oldPassword, @RequestParam String newPassword) {
        authService.changePassword(username, oldPassword, newPassword);
        return ResponseEntity.ok().build();
    }
}
