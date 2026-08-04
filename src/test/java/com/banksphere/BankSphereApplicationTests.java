package com.banksphere;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class BankSphereApplicationTests {

    @Test
    @DisplayName("Context loads without errors")
    void contextLoads() {
        // given
        // when
        // then
        // TODO: implement context load check
        assertThat(true).isTrue();
    }
}
