package com.banksphere.modules.auth.service.impl;

import com.banksphere.core.exception.DuplicateResourceException;
import com.banksphere.core.exception.ResourceNotFoundException;
import com.banksphere.core.exception.UnauthorizedAccessException;
import com.banksphere.core.security.jwt.JwtTokenProvider;
import com.banksphere.modules.auth.dto.request.*;
import com.banksphere.modules.auth.dto.response.AuthTokenResponseDTO;
import com.banksphere.modules.auth.entity.PasswordResetToken;
import com.banksphere.modules.auth.entity.RefreshToken;
import com.banksphere.modules.auth.entity.Role;
import com.banksphere.modules.auth.entity.User;
import com.banksphere.modules.auth.mapper.AuthMapper;
import com.banksphere.modules.auth.repository.PasswordResetTokenRepository;
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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long REFRESH_TOKEN_VALIDITY_MS = 7L * 24 * 60 * 60 * 1000;
    private static final long PASSWORD_RESET_EXPIRY_MINUTES = 15;

    // ======================== LOGIN ========================

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
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            log.info("Login successful for user: {}", request.getUsername());
            return buildAuthResponse(accessToken, refreshToken, user.getUsername(), roles, user.isMfaEnabled());

        } catch (BadCredentialsException e) {
            handleFailedLogin(user, request.getUsername());
            throw new UnauthorizedAccessException("Invalid username or password");
        }
    }

    // ======================== REFRESH TOKEN ========================

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
                user.getRoles().stream()
                        .map(r -> (GrantedAuthority) () -> r.getName())
                        .collect(Collectors.toList()));

        String newAccessToken = jwtTokenProvider.generateAccessToken(auth);
        String newRefreshToken = createRefreshToken(user.getId());

        List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        return buildAuthResponse(newAccessToken, newRefreshToken, user.getUsername(), roles, false);
    }

    // ======================== LOGOUT ========================

    @Override
    public void logout(String refreshTokenStr) {
        log.info("Logging out — revoking refresh token");
        refreshTokenRepository.findByToken(refreshTokenStr).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    // ======================== MFA VERIFY ========================

    @Override
    public AuthTokenResponseDTO verifyMfa(MfaVerificationDTO request) {
        log.info("Verifying MFA for user: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", request.getUsername()));

        if (!user.isMfaEnabled()) {
            throw new UnauthorizedAccessException("MFA is not enabled for this user");
        }

        // MFA code must be exactly 6 digits
        if (request.getMfaCode() == null || !request.getMfaCode().matches("\\d{6}")) {
            throw new UnauthorizedAccessException("Invalid MFA code format");
        }

        // TODO: Integrate TOTP library (e.g., aerogear-otp or java-otp) to validate
        // against user.getMfaSecret(). For now, this validates format only.
        // Example with GoogleAuthenticator: new GoogleAuthenticator().authorize(user.getMfaSecret(), code)
        log.warn("MFA TOTP validation not yet integrated. Accepting format-valid code for user: {}", request.getUsername());

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getUsername(), null,
                user.getRoles().stream()
                        .map(r -> (GrantedAuthority) () -> r.getName())
                        .collect(Collectors.toList()));

        String accessToken = jwtTokenProvider.generateAccessToken(auth);
        String refreshToken = createRefreshToken(user.getId());
        List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());

        return buildAuthResponse(accessToken, refreshToken, user.getUsername(), roles, false);
    }

    // ======================== PASSWORD RESET ========================

    @Override
    public void initiatePasswordReset(String email) {
        log.info("Initiating password reset for: {}", email);

        // Look up user — always return 200 to avoid email enumeration
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            log.warn("Password reset requested for unknown email: {}", email);
            return; // Silently return — don't expose if email exists
        }

        User user = userOpt.get();

        // Invalidate any existing unused tokens for this user
        passwordResetTokenRepository.invalidateAllTokensForUser(user.getId());

        // Generate secure raw token (UUID-based)
        String rawToken = UUID.randomUUID().toString();
        String tokenHash = hashToken(rawToken);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .tokenHash(tokenHash)
                .userId(user.getId())
                .expiryDate(LocalDateTime.now().plusMinutes(PASSWORD_RESET_EXPIRY_MINUTES))
                .used(false)
                .build();
        passwordResetTokenRepository.save(resetToken);

        // TODO: Send email with reset link containing rawToken
        // emailService.sendPasswordResetEmail(user.getEmail(), rawToken);
        log.info("Password reset token generated for user: {}. Token (dev only): {}", user.getUsername(), rawToken);
    }

    @Override
    public void resetPassword(PasswordResetRequestDTO request) {
        log.info("Resetting password with token");

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        String tokenHash = hashToken(request.getToken());
        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new UnauthorizedAccessException("Invalid or expired password reset token"));

        if (resetToken.isUsed()) {
            throw new UnauthorizedAccessException("Password reset token has already been used");
        }
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedAccessException("Password reset token has expired. Please request a new one.");
        }

        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", resetToken.getUserId()));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setLastPasswordChangedAt(LocalDateTime.now());
        user.setFailedLoginAttempts(0);
        user.setAccountNonLocked(true);
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        // Revoke all refresh tokens — force re-login
        refreshTokenRepository.revokeAllByUserId(user.getId());

        log.info("Password reset completed for user: {}", user.getUsername());
    }

    // ======================== CHANGE PASSWORD ========================

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
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from the current password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setLastPasswordChangedAt(LocalDateTime.now());
        userRepository.save(user);

        // Revoke all refresh tokens — force re-login
        refreshTokenRepository.revokeAllByUserId(user.getId());

        log.info("Password changed successfully for user: {}", username);
    }

    // ======================== REGISTER ========================

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

    private void handleFailedLogin(User user, String username) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
            user.setAccountNonLocked(false);
            log.warn("Account locked for user: {} after {} failed attempts", username, MAX_FAILED_ATTEMPTS);
        }
        userRepository.save(user);
    }

    /**
     * Creates a new refresh token for the given user.
     * Uses bulk revocation to avoid N+1 problem.
     */
    private String createRefreshToken(UUID userId) {
        // Bulk-revoke all existing active tokens for this user
        refreshTokenRepository.revokeAllByUserId(userId);

        RefreshToken token = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .userId(userId)
                .expiryDate(Instant.now().plusMillis(REFRESH_TOKEN_VALIDITY_MS))
                .revoked(false)
                .build();
        refreshTokenRepository.save(token);
        return token.getToken();
    }

    private AuthTokenResponseDTO buildAuthResponse(String accessToken, String refreshToken,
                                                    String username, List<String> roles, boolean mfaRequired) {
        return AuthTokenResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .username(username)
                .roles(roles)
                .mfaRequired(mfaRequired)
                .build();
    }

    /**
     * SHA-256 hash of the raw token. Only the hash is stored in DB.
     */
    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
