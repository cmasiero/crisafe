package com.crisafe;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

/**
 * Service for hashing and verifying passwords using the Argon2id algorithm.
 *
 * <p>The salt is loaded once from {@code crypto.properties} on the classpath.
 * Argon2id parameters follow OWASP recommendations:
 * <ul>
 *   <li>Memory: 64 MB</li>
 *   <li>Iterations: 3</li>
 *   <li>Parallelism: 4</li>
 *   <li>Hash length: 32 bytes</li>
 * </ul>
 *
 * <p><b>Note:</b> A shared, fixed salt (from a property file) provides pepper-like
 * protection but is not a substitute for a per-password random salt in high-security
 * scenarios. Keep the salt value secret and change it before deploying to production.
 */
public class CryptoService {

    private static final String PROPERTIES_FILE = "crypto.properties";
    private static final String SALT_KEY        = "crypto.salt";

    // Argon2id tuning parameters (OWASP recommended minimums)
    private static final int ITERATIONS   = 3;
    private static final int MEMORY_KB    = 64 * 1024; // 64 MB
    private static final int PARALLELISM  = 4;
    private static final int HASH_BYTES   = 32;

    private final byte[] salt;

    /**
     * Creates a {@code CryptoService} by reading the salt from {@code crypto.properties}.
     *
     * @throws IllegalStateException if the properties file or the salt key is missing
     */
    public CryptoService() {
        this.salt = loadSalt();
    }

    /**
     * Hashes a plain-text password with Argon2id and returns a Base64-encoded hash.
     *
     * @param password the plain-text password (must not be null or empty)
     * @return Base64-encoded Argon2id hash
     */
    public String encode(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password must not be null or empty");
        }
        byte[] hash = argon2Hash(password.toCharArray());
        return Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Hashes a plain-text password with Argon2id using the provided salt and returns the raw 32-byte hash.
     * Use this overload when deriving an AES key: generate a random salt per file, store it alongside
     * the ciphertext, and pass it back here to re-derive the same key for decryption.
     *
     * @param password the plain-text password (must not be null or empty)
     * @param salt     the per-use salt (must not be null or empty; recommended: 16 random bytes)
     * @return raw 32-byte Argon2id hash, suitable for use as an AES-256 key
     */
    public byte[] encode(String password, byte[] salt) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password must not be null or empty");
        }
        if (salt == null || salt.length == 0) {
            throw new IllegalArgumentException("Salt must not be null or empty");
        }
        return argon2Hash(password.toCharArray(), salt);
    }

    /**
     * Verifies a plain-text password against a previously encoded hash.
     *
     * @param password    the plain-text password to verify
     * @param encodedHash the Base64-encoded hash produced by {@link #encode(String)}
     * @return {@code true} if the password matches the hash, {@code false} otherwise
     */
    public boolean verify(String password, String encodedHash) {
        if (password == null || password.isEmpty() || encodedHash == null || encodedHash.isEmpty()) {
            return false;
        }
        byte[] candidate = argon2Hash(password.toCharArray());
        byte[] expected  = Base64.getDecoder().decode(encodedHash);
        return constantTimeEquals(candidate, expected);
    }

    // ---------- private helpers ----------

    private byte[] argon2Hash(char[] password) {
        return argon2Hash(password, salt);
    }

    private byte[] argon2Hash(char[] password, byte[] salt) {
        Argon2Parameters params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withSalt(salt)
                .withIterations(ITERATIONS)
                .withMemoryAsKB(MEMORY_KB)
                .withParallelism(PARALLELISM)
                .build();

        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(params);

        byte[] passwordBytes = toBytes(password);
        byte[] hash = new byte[HASH_BYTES];
        generator.generateBytes(passwordBytes, hash);
        wipe(passwordBytes);
        return hash;
    }

    /** Converts a char array to UTF-8 bytes without going through a String. */
    private static byte[] toBytes(char[] chars) {
        return new String(chars).getBytes(StandardCharsets.UTF_8);
    }

    /** Constant-time comparison to prevent timing attacks. */
    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }
        int diff = 0;
        for (int i = 0; i < a.length; i++) {
            diff |= (a[i] ^ b[i]);
        }
        return diff == 0;
    }

    /** Zeroes out a byte array after use. */
    private static void wipe(byte[] bytes) {
        java.util.Arrays.fill(bytes, (byte) 0);
    }

    private static byte[] loadSalt() {
        Properties props = new Properties();
        try (InputStream is = CryptoService.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (is == null) {
                throw new IllegalStateException("Property file not found on classpath: " + PROPERTIES_FILE);
            }
            props.load(is);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + PROPERTIES_FILE, e);
        }

        String saltValue = props.getProperty(SALT_KEY);
        if (saltValue == null || saltValue.isBlank()) {
            throw new IllegalStateException("Missing or empty property '" + SALT_KEY + "' in " + PROPERTIES_FILE);
        }
        return saltValue.getBytes(StandardCharsets.UTF_8);
    }
}
