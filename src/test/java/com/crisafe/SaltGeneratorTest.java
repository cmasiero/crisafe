package com.crisafe;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class SaltGeneratorTest {

    @Test
    void generate_returnsNonNullNonBlankString() {
        String salt = SaltGenerator.generate();
        assertNotNull(salt);
        assertFalse(salt.isBlank());
    }

    @Test
    void generate_defaultLengthDecodesTo32Bytes() {
        String salt = SaltGenerator.generate();
        byte[] decoded = Base64.getDecoder().decode(salt);
        assertEquals(SaltGenerator.DEFAULT_SALT_BYTES, decoded.length);
    }

    @Test
    void generate_customLengthDecodesCorrectly() {
        String salt = SaltGenerator.generate(24);
        byte[] decoded = Base64.getDecoder().decode(salt);
        assertEquals(24, decoded.length);
    }

    @Test
    void generate_twoCallsProduceDifferentSalts() {
        String salt1 = SaltGenerator.generate();
        String salt2 = SaltGenerator.generate();
        assertNotEquals(salt1, salt2);
    }

    @Test
    void generate_belowMinimumThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> SaltGenerator.generate(8));
    }

    @Test
    void generate_exactMinimumDoesNotThrow() {
        assertDoesNotThrow(() -> SaltGenerator.generate(16));
    }
}
