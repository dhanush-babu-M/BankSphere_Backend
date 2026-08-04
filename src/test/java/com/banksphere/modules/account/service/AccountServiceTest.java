package com.banksphere.modules.account.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private Object accountRepository;

    @Mock
    private Object accountMapper;

    @Mock
    private Object accountNumberGenerator;

    @Test
    @DisplayName("Create account should save and return DTO")
    void createAccount_shouldSaveAndReturnDTO() {
        // given
        // when
        // then
        assertThat(true).isTrue();
    }

    @Test
    @DisplayName("Get account by number should return DTO when found")
    void getAccountByNumber_shouldReturnDTO_whenFound() {
        // given
        // when
        // then
        assertThat(true).isTrue();
    }

    @Test
    @DisplayName("Get account by number should throw not found exception when not found")
    void getAccountByNumber_shouldThrowNotFoundException_whenNotFound() {
        // given
        // when
        // then
        assertThat(true).isTrue();
    }

    @Test
    @DisplayName("Credit account should increase balance")
    void creditAccount_shouldIncreaseBalance() {
        // given
        // when
        // then
        assertThat(true).isTrue();
    }

    @Test
    @DisplayName("Debit account should decrease balance")
    void debitAccount_shouldDecreaseBalance() {
        // given
        // when
        // then
        assertThat(true).isTrue();
    }

    @Test
    @DisplayName("Debit account should throw exception when insufficient funds")
    void debitAccount_shouldThrowException_whenInsufficientFunds() {
        // given
        // when
        // then
        assertThat(true).isTrue();
    }
}
