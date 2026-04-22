package com.crisafe.util;

import com.crisafe.service.CryptoService;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for generating cryptographically secure random salts.
 *
 * <p>Uses {@link SecureRandom} (DRBG) as the entropy source. The generated
 * salt is returned as a Base64-encoded string suitable for storing in a
 * properties file and passing to {@link CryptoService}.
 */
public final class SaltGenerator {

    /** Recommended minimum salt length in bytes (OWASP: >= 16 bytes). */
    public static final int DEFAULT_SALT_BYTES = 32;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private SaltGenerator() {
        // utility class – no instances
    }

    /**
     * Generates a random salt of {@value #DEFAULT_SALT_BYTES} bytes,
     * returned as a Base64-encoded string.
     *
     * @return Base64-encoded random salt (44 characters)
     */
    public static String generate() {
        return generate(DEFAULT_SALT_BYTES);
    }

    /**
     * Generates a random salt of the given byte length,
     * returned as a Base64-encoded string.
     *
     * @param byteLength number of random bytes (must be >= 16)
     * @return Base64-encoded random salt
     * @throws IllegalArgumentException if {@code byteLength} is less than 16
     */
    public static String generate(int byteLength) {
        if (byteLength < 16) {
            throw new IllegalArgumentException("Salt must be at least 16 bytes, got: " + byteLength);
        }
        byte[] bytes = new byte[byteLength];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
