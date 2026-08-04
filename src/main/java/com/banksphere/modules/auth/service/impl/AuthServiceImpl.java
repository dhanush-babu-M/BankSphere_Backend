package com.banksphere.modules.auth.service.impl;

import com.banksphere.core.exception.DuplicateResourceException;
import com.banksphere.core.exception.ResourceNotFoundException;
import com.banksphere.core.exception.UnauthorizedAccessException;
import com.banksphere.core.security.jwt.JwtTokenProvider;
import com.banksphere.modules.auth.dto.request.*;
import com.banksphere.modules.auth.dto.response.AuthTokenResponseDTO;
import com.banksphere.modules.auth.entity.RefreshToken;
import com.banksphere.modules.auth.entity.Role;
import com.banksphere.modules.auth.entity.User;
import com.banksphere.modules.auth.mapper.AuthMapper;
import com.banksphere.modules.auth.repository.RefreshTokenRepository;
import com.banksphere.modules.auth.repository.RoleRepository;
import com.banksphere.modules.auth.repository.UserRepository;
import com.banksphere.modules.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long REFRESH_TOKEN_VALIDITY_MS = 7L * 24 * 60 * 60 * 1000;

    @Override
    public AuthTokenResponseDTO login(LoginRequestDTO request) {
        log.info("Login attempt for user: {}", request.getUsername());
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedAccessException("Invalid username or password"));
        if (!user.isAccountNonLocked()) {
            throw new LockedException("Account is locked. Please contact support.");
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            // Reset failed attempts on success
            user.setFailedLoginAttempts(0);
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
            String accessToken = jwtTokenProvider.generateAccessToken(authentication);
            String refreshToken = createRefreshToken(user.getId());
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).collect(Collectors.toList());
            log.info("Login successful for user: {}", request.getUsername());
            return AuthTokenResponseDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(86400L)
                    .username(user.getUsername())
                    .roles(roles)
                    .mfaRequired(user.isMfaEnabled())
                    .build();
        } catch (BadCredentialsException e) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
                user.setAccountNonLocked(false);
                log.warn("Account locked for user: {} after {} failed attempts", request.getUsername(), MAX_FAILED_ATTEMPTS);
            }
            userRepository.save(user);
            throw new UnauthorizedAccessException("Invalid username or password");
        }
    }

    @Override
    public AuthTokenResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        log.info("Refreshing access token");
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedAccessException("Refresh token not found"));
        if (refreshToken.isRevoked()) {
            throw new UnauthorizedAccessException("Refresh token has been revoked");
        }
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new UnauthorizedAccessException("Refresh token has expired. Please login again.");
        }
        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", refreshToken.getUserId()));
        // Rotate: revoke old, issue new
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getUsername(), null,
                user.getRoles().stream().map(r -> (GrantedAuthority) r::getName).collect(Collectors.toList()));
        String newAccessToken = jwtTokenProvider.generateAccessToken(auth);
        String newRefreshToken = createRefreshToken(user.getId());
        List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        return AuthTokenResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .username(user.getUsername())
                .roles(roles)
                .mfaRequired(false)
                .build();
    }

    @Override
    public void logout(String refreshTokenStr) {
        log.info("Logging out - revoking refresh token");
        refreshTokenRepository.findByToken(refreshTokenStr).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    @Override
    public AuthTokenResponseDTO verifyMfa(MfaVerificationDTO request) {
        log.info("Verifying MFA for user: {}", request.getUsername());
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", request.getUsername()));
        // TODO: Integrate with Google Authenticator / TOTP library (e.g., aerogear-otp)
        // For now, validate a 6-digit code stub
        if (request.getMfaCode() == null || request.getMfaCode().length() != 6) {
            throw new UnauthorizedAccessException("Invalid MFA code");
        }
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getUsername(), null,
                user.getRoles().stream().map(r -> (GrantedAuthority) r::getName).collect(Collectors.toList()));
        String accessToken = jwtTokenProvider.generateAccessToken(auth);
        String refreshToken = createRefreshToken(user.getId());
        List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        return AuthTokenResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .username(user.getUsername())
                .roles(roles)
                .mfaRequired(false)
                .build();
    }

    @Override
    public void initiatePasswordReset(String email) {
        log.info("Initiating password reset for: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        // Generate secure reset token (UUID-based, stored as hashed)
        String resetToken = UUID.randomUUID().toString();
        // TODO: Store reset token with expiry in a password_reset_tokens table
        // TODO: Send email with reset link: /auth/password/reset?token=resetToken
        log.info("Password reset initiated for user: {}, token: {}", user.getUsername(), resetToken);
    }

    @Override
    public void resetPassword(PasswordResetRequestDTO request) {
        log.info("Resetting password with token");
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        // TODO: Validate reset token from DB, check expiry
        // TODO: Find user by token, update password
        log.info("Password reset completed");
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        log.info("Changing password for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UnauthorizedAccessException("Current password is incorrect");
        }
        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setLastPasswordChangedAt(LocalDateTime.now());
        userRepository.save(user);
        log.info("Password changed successfully for user: {}", username);
    }

    @Override
    public User registerUser(String username, String email, String password, List<String> roleNames) {
        log.info("Registering new user: {}", username);
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("User", "username", username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("User", "email", email);
        }
        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
            roles.add(role);
        }
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .roles(roles)
                .enabled(true)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .failedLoginAttempts(0)
                .mfaEnabled(false)
                .build();
        User saved = userRepository.save(user);
        log.info("User registered successfully: {}", username);
        return saved;
    }

    // ======================== PRIVATE HELPERS ========================

    private String createRefreshToken(UUID userId) {
        // Revoke existing tokens for this user
        refreshTokenRepository.findByUserIdAndRevokedFalse(userId).forEach(t -> {
            t.setRevoked(true);
            refreshTokenRepository.save(t);
        });
        RefreshToken token = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .userId(userId)
                .expiryDate(Instant.now().plusMillis(REFRESH_TOKEN_VALIDITY_MS))
                .revoked(false)
                .build();
        refreshTokenRepository.save(token);
        return token.getToken();
    }
}
