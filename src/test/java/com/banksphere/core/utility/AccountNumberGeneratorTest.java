package com.banksphere.core.utility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AccountNumberGeneratorTest {

    private AccountNumberGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new AccountNumberGenerator();
    }

    @Test
    @DisplayName("Generate account number should return 16-character string with prefix and year")
    void generateAccountNumber_shouldReturn16DigitString() {
        String accountNumber = generator.generateAccountNumber();

        assertThat(accountNumber).isNotNull();
        assertThat(accountNumber).hasSize(16);
        assertThat(accountNumber).startsWith("BSP" + Year.now().getValue());
    }

    @Test
    @DisplayName("Generate account number should be unique across multiple generations")
    void generateAccountNumber_shouldBeUnique() {
        Set<String> accountNumbers = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            accountNumbers.add(generator.generateAccountNumber());
        }

        assertThat(accountNumbers).hasSize(100);
    }

    @Test
    @DisplayName("Is valid account number with valid number should return true")
    void isValidAccountNumber_withValidNumber_shouldReturnTrue() {
        String accountNumber = generator.generateAccountNumber();

        assertThat(generator.isValidAccountNumber(accountNumber)).isTrue();
    }

    @Test
    @DisplayName("Is valid account number with null input should return false")
    void isValidAccountNumber_withNullInput_shouldReturnFalse() {
        assertThat(generator.isValidAccountNumber(null)).isFalse();
    }

    @Test
    @DisplayName("Is valid account number with short number or invalid prefix should return false")
    void isValidAccountNumber_withShortNumber_shouldReturnFalse() {
        assertThat(generator.isValidAccountNumber("BSP123")).isFalse();
        assertThat(generator.isValidAccountNumber("XYZ1234567890123")).isFalse();
        assertThat(generator.isValidAccountNumber("")).isFalse();
    }

    @Test
    @DisplayName("Generate virtual account number should start with given prefix and have 16 chars")
    void generateVirtualAccountNumber_shouldHaveCorrectPrefixAndLength() {
        String virtualAcc = generator.generateVirtualAccountNumber("VIRT");

        assertThat(virtualAcc).hasSize(16);
        assertThat(virtualAcc).startsWith("VIRT");
    }
}

