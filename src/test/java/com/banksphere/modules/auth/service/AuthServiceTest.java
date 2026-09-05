package com.banksphere.modules.auth.service;

import com.banksphere.core.exception.UnauthorizedAccessException;
import com.banksphere.core.security.jwt.JwtTokenProvider;
import com.banksphere.modules.auth.dto.request.ChangePasswordRequestDTO;
import com.banksphere.modules.auth.dto.request.LoginRequestDTO;
import com.banksphere.modules.auth.dto.request.RefreshTokenRequestDTO;
import com.banksphere.modules.auth.dto.response.AuthTokenResponseDTO;
import com.banksphere.modules.auth.entity.RefreshToken;
import com.banksphere.modules.auth.entity.Role;
import com.banksphere.modules.auth.entity.User;
import com.banksphere.modules.auth.mapper.AuthMapper;
import com.banksphere.modules.auth.repository.PasswordResetTokenRepository;
import com.banksphere.modules.auth.repository.RefreshTokenRepository;
import com.banksphere.modules.auth.repository.RoleRepository;
import com.banksphere.modules.auth.repository.UserRepository;
import com.banksphere.modules.auth.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthMapper authMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private User sampleUser;
    private Role customerRole;

    @BeforeEach
    void setUp() {
        customerRole = Role.builder().id(1L).name("ROLE_CUSTOMER").build();

        sampleUser = User.builder()
                .id(UUID.randomUUID())
                .username("test_user")
                .password("$2a$10$hashedPassword")
                .email("test@banksphere.com")
                .roles(Set.of(customerRole))
                .accountNonLocked(true)
                .enabled(true)
                .failedLoginAttempts(0)
                .build();
    }

    @Test
    @DisplayName("Login should return auth token response with valid credentials")
    void login_shouldReturnAuthTokenResponse_withValidCredentials() {
        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("test_user")
                .password("Password@123")
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                "test_user", "Password@123", List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

        when(userRepository.findByUsername("test_user")).thenReturn(Optional.of(sampleUser));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtTokenProvider.generateAccessToken(auth)).thenReturn("mock-access-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

        AuthTokenResponseDTO response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("mock-access-token");
        assertThat(response.getUsername()).isEqualTo("test_user");
        assertThat(sampleUser.getFailedLoginAttempts()).isEqualTo(0);
        verify(userRepository).save(sampleUser);
    }

    @Test
    @DisplayName("Login should throw exception with invalid credentials and record failed attempt")
    void login_shouldThrowException_withInvalidCredentials() {
        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("test_user")
                .password("WrongPassword")
                .build();

        when(userRepository.findByUsername("test_user")).thenReturn(Optional.of(sampleUser));
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(UnauthorizedAccessException.class, () -> authService.login(request));

        assertThat(sampleUser.getFailedLoginAttempts()).isEqualTo(1);
        verify(userRepository).save(sampleUser);
    }

    @Test
    @DisplayName("Refresh token should return new access token with valid refresh token")
    void refreshToken_shouldReturnNewAccessToken_withValidRefreshToken() {
        RefreshTokenRequestDTO request = RefreshTokenRequestDTO.builder()
                .refreshToken("valid-refresh-token")
                .build();

        RefreshToken oldToken = RefreshToken.builder()
                .id(UUID.randomUUID())
                .token("valid-refresh-token")
                .userId(sampleUser.getId())
                .expiryDate(Instant.now().plusSeconds(3600))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByToken("valid-refresh-token")).thenReturn(Optional.of(oldToken));
        when(userRepository.findById(sampleUser.getId())).thenReturn(Optional.of(sampleUser));
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn("new-access-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

        AuthTokenResponseDTO response = authService.refreshToken(request);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(oldToken.isRevoked()).isTrue();
    }

    @Test
    @DisplayName("Refresh token should throw exception with expired refresh token")
    void refreshToken_shouldThrowException_withExpiredRefreshToken() {
        RefreshTokenRequestDTO request = RefreshTokenRequestDTO.builder()
                .refreshToken("expired-refresh-token")
                .build();

        RefreshToken expiredToken = RefreshToken.builder()
                .token("expired-refresh-token")
                .userId(sampleUser.getId())
                .expiryDate(Instant.now().minusSeconds(3600))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByToken("expired-refresh-token")).thenReturn(Optional.of(expiredToken));

        assertThrows(UnauthorizedAccessException.class, () -> authService.refreshToken(request));
    }

    @Test
    @DisplayName("Logout should revoke refresh token")
    void logout_shouldRevokeRefreshToken() {
        RefreshToken token = RefreshToken.builder()
                .token("sample-refresh-token")
                .revoked(false)
                .build();
        when(refreshTokenRepository.findByToken("sample-refresh-token")).thenReturn(Optional.of(token));

        authService.logout("sample-refresh-token");

        assertThat(token.isRevoked()).isTrue();
        verify(refreshTokenRepository).save(token);
    }

    @Test
    @DisplayName("Change password should update password successfully when current matches")
    void changePassword_shouldUpdatePasswordSuccessfully() {
        when(userRepository.findByUsername("test_user")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("OldPassword@123", sampleUser.getPassword())).thenReturn(true);
        when(passwordEncoder.matches("NewPassword@123", sampleUser.getPassword())).thenReturn(false);
        when(passwordEncoder.encode("NewPassword@123")).thenReturn("$2a$10$newHashedPassword");

        authService.changePassword("test_user", "OldPassword@123", "NewPassword@123");

        assertThat(sampleUser.getPassword()).isEqualTo("$2a$10$newHashedPassword");
        verify(userRepository).save(sampleUser);
        verify(refreshTokenRepository).revokeAllByUserId(sampleUser.getId());
    }
}

