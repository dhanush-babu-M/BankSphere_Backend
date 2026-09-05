package com.banksphere.core.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @Mock
    private JwtProperties jwtProperties;

    private JwtTokenProvider jwtTokenProvider;

    private static final String TEST_SECRET = "BankSphereSecretKeyForJWTTokenGenerationMustBeAtLeast256BitsLong!";
    private static final long TEST_EXPIRATION_MS = 3600000L; // 1 hour
    private static final long TEST_REFRESH_EXPIRATION_MS = 86400000L; // 24 hours

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(jwtProperties);
    }

    @Test
    @DisplayName("Generate access token should return valid JWT token with claims")
    void generateAccessToken_shouldReturnValidToken() {
        when(jwtProperties.getSecret()).thenReturn(TEST_SECRET);
        when(jwtProperties.getExpirationMs()).thenReturn(TEST_EXPIRATION_MS);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                "john_doe", "password", List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

        String token = jwtTokenProvider.generateAccessToken(auth);

        assertThat(token).isNotBlank();
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.getUsernameFromToken(token)).isEqualTo("john_doe");
        assertThat(jwtTokenProvider.getRolesFromToken(token)).contains("ROLE_CUSTOMER");
    }

    @Test
    @DisplayName("Generate refresh token should return valid token containing username")
    void generateRefreshToken_shouldReturnValidToken() {
        when(jwtProperties.getSecret()).thenReturn(TEST_SECRET);
        when(jwtProperties.getRefreshExpirationMs()).thenReturn(TEST_REFRESH_EXPIRATION_MS);

        String refreshToken = jwtTokenProvider.generateRefreshToken("jane_doe");

        assertThat(refreshToken).isNotBlank();
        assertThat(jwtTokenProvider.validateToken(refreshToken)).isTrue();
        assertThat(jwtTokenProvider.getUsernameFromToken(refreshToken)).isEqualTo("jane_doe");
    }

    @Test
    @DisplayName("Validate token with valid token should return true")
    void validateToken_withValidToken_shouldReturnTrue() {
        when(jwtProperties.getSecret()).thenReturn(TEST_SECRET);
        when(jwtProperties.getExpirationMs()).thenReturn(TEST_EXPIRATION_MS);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                "testuser", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        String token = jwtTokenProvider.generateAccessToken(auth);

        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("Validate token with expired token should return false")
    void validateToken_withExpiredToken_shouldReturnFalse() {
        when(jwtProperties.getSecret()).thenReturn(TEST_SECRET);
        // Expiration in the past
        when(jwtProperties.getExpirationMs()).thenReturn(-1000L);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                "expired_user", null, List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
        String token = jwtTokenProvider.generateAccessToken(auth);

        assertThat(jwtTokenProvider.validateToken(token)).isFalse();
    }

    @Test
    @DisplayName("Validate token with invalid token should return false")
    void validateToken_withInvalidToken_shouldReturnFalse() {
        when(jwtProperties.getSecret()).thenReturn(TEST_SECRET);

        assertThat(jwtTokenProvider.validateToken("invalid.jwt.token")).isFalse();
        assertThat(jwtTokenProvider.validateToken("")).isFalse();
    }
}

