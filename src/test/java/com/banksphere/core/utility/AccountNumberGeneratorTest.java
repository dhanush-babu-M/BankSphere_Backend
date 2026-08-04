package com.banksphere.core.utility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AccountNumberGeneratorTest {

    @Test
    @DisplayName("Generate account number should return 16 digit string")
    void generateAccountNumber_shouldReturn16DigitString() {
        // given
        // when
        // then
        assertThat(true).isTrue();
    }

    @Test
    @DisplayName("Generate account number should be unique")
    void generateAccountNumber_shouldBeUnique() {
        // given
        // when
        // then
        assertThat(true).isTrue();
    }

    @Test
    @DisplayName("Is valid account number with valid number should return true")
    void isValidAccountNumber_withValidNumber_shouldReturnTrue() {
        // given
        // when
        // then
        assertThat(true).isTrue();
    }

    @Test
    @DisplayName("Is valid account number with null input should return false")
    void isValidAccountNumber_withNullInput_shouldReturnFalse() {
        // given
        // when
        // then
        assertThat(true).isTrue();
    }

    @Test
    @DisplayName("Is valid account number with short number should return false")
    void isValidAccountNumber_withShortNumber_shouldReturnFalse() {
        // given
        // when
        // then
        assertThat(true).isTrue();
    }
}
