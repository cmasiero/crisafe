package com.crisafe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CryptoServiceTest {

    private CryptoService crypto;

    @BeforeEach
    void setUp() {
        crypto = new CryptoService();
    }

    @Test
    void encode_returnsNonNullBase64String() {
        String hash = crypto.encode("mySecret123");
        assertNotNull(hash);
        assertFalse(hash.isBlank());
    }

    @Test
    void encode_samePasswordProducesSameHash() {
        String hash1 = crypto.encode("samePassword");
        String hash2 = crypto.encode("samePassword");
        assertEquals(hash1, hash2);
    }

    @Test
    void encode_differentPasswordsProduceDifferentHashes() {
        String hash1 = crypto.encode("password1");
        String hash2 = crypto.encode("password2");
        assertNotEquals(hash1, hash2);
    }

    @Test
    void verify_correctPasswordReturnsTrue() {
        String hash = crypto.encode("correctHorseBatteryStaple");
        assertTrue(crypto.verify("correctHorseBatteryStaple", hash));
    }

    @Test
    void verify_wrongPasswordReturnsFalse() {
        String hash = crypto.encode("correctPassword");
        assertFalse(crypto.verify("wrongPassword", hash));
    }

    @Test
    void verify_nullPasswordReturnsFalse() {
        String hash = crypto.encode("somePassword");
        assertFalse(crypto.verify(null, hash));
    }

    @Test
    void verify_nullHashReturnsFalse() {
        assertFalse(crypto.verify("somePassword", null));
    }

    @Test
    void encode_nullPasswordThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> crypto.encode(null));
    }

    @Test
    void encode_emptyPasswordThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> crypto.encode(""));
    }
}
