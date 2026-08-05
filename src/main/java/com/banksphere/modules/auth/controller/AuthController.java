package com.banksphere.modules.auth.controller;

import com.banksphere.modules.auth.dto.request.*;
import com.banksphere.modules.auth.dto.response.AuthTokenResponseDTO;
import com.banksphere.modules.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication API", description = "Endpoints for login, token refresh, logout and password management")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login with username and password")
    public ResponseEntity<AuthTokenResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token using a valid refresh token")
    public ResponseEntity<AuthTokenResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout — revokes the refresh token")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequestDTO request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mfa/verify")
    @Operation(summary = "Verify MFA OTP code")
    public ResponseEntity<AuthTokenResponseDTO> verifyMfa(@Valid @RequestBody MfaVerificationDTO request) {
        return ResponseEntity.ok(authService.verifyMfa(request));
    }

    @PostMapping("/password/reset-request")
    @Operation(summary = "Request a password reset link via email")
    public ResponseEntity<Void> initiatePasswordReset(@RequestParam String email) {
        authService.initiatePasswordReset(email);
        // Always return 200 to avoid email enumeration attacks
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/reset")
    @Operation(summary = "Reset password using token received via email")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetRequestDTO request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/password/change")
    @Operation(summary = "Change password (authenticated users)")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequestDTO request) {
        authService.changePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
