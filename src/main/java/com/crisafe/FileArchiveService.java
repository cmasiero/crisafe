package com.crisafe;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;

/**
 * Encrypts and decrypts JSON archives using AES-256-GCM.
 *
 * <p>File layout: {@code [salt (16 bytes)][IV (12 bytes)][ciphertext + GCM tag (16 bytes)]}.
 * The AES key is derived from the user password and the per-file random salt via Argon2id
 * ({@link CryptoService#encode(String, byte[])}).
 */
public class FileArchiveService {

    public static final String EXTENSION = ".crisafe";

    private static final int SALT_BYTES  = 16;
    private static final int IV_BYTES    = 12;
    private static final int GCM_TAG_BITS = 128;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final CryptoService cryptoService;

    public FileArchiveService(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    /**
     * Encrypts {@code jsonContent} with an Argon2id-derived AES-256-GCM key and writes
     * the result to {@code outputPath}.
     *
     * @param jsonContent plain-text JSON to protect
     * @param password    user password for key derivation
     * @param outputPath  destination file (created or overwritten)
     */
    public void encrypt(String jsonContent, String password, Path outputPath) throws Exception {
        byte[] salt = new byte[SALT_BYTES];
        byte[] iv   = new byte[IV_BYTES];
        SECURE_RANDOM.nextBytes(salt);
        SECURE_RANDOM.nextBytes(iv);

        byte[] key        = cryptoService.encode(password, salt);
        byte[] plaintext  = jsonContent.getBytes(StandardCharsets.UTF_8);
        byte[] ciphertext = aesgcmEncrypt(key, iv, plaintext);

        byte[] fileBytes = new byte[SALT_BYTES + IV_BYTES + ciphertext.length];
        System.arraycopy(salt,       0, fileBytes, 0,                     SALT_BYTES);
        System.arraycopy(iv,         0, fileBytes, SALT_BYTES,            IV_BYTES);
        System.arraycopy(ciphertext, 0, fileBytes, SALT_BYTES + IV_BYTES, ciphertext.length);

        Files.write(outputPath, fileBytes);
    }

    /**
     * Decrypts a {@code .crisafe} file and returns the original JSON string.
     *
     * @param filePath path to the encrypted archive
     * @param password password used when the archive was created
     * @return decrypted JSON string
     * @throws Exception if the password is wrong or the file is corrupted
     */
    public String decrypt(Path filePath, String password) throws Exception {
        byte[] fileBytes = Files.readAllBytes(filePath);
        int minLen = SALT_BYTES + IV_BYTES + (GCM_TAG_BITS / 8);
        if (fileBytes.length < minLen) {
            throw new IllegalArgumentException("File too short to be a valid archive");
        }

        byte[] salt       = new byte[SALT_BYTES];
        byte[] iv         = new byte[IV_BYTES];
        byte[] ciphertext = new byte[fileBytes.length - SALT_BYTES - IV_BYTES];

        System.arraycopy(fileBytes, 0,                    salt,       0, SALT_BYTES);
        System.arraycopy(fileBytes, SALT_BYTES,           iv,         0, IV_BYTES);
        System.arraycopy(fileBytes, SALT_BYTES + IV_BYTES, ciphertext, 0, ciphertext.length);

        byte[] key       = cryptoService.encode(password, salt);
        byte[] plaintext = aesgcmDecrypt(key, iv, ciphertext);

        return new String(plaintext, StandardCharsets.UTF_8);
    }


    public static Path[] listArchives() throws IOException {
        return listArchives(defaulPath());
    }

    /**
     * Returns all {@code .crisafe} files found in {@code directory}, sorted by name.
     */
    public static Path[] listArchives(Path directory) throws IOException {
        if (!Files.isDirectory(directory)) return new Path[0];
        try (var stream = Files.list(directory)) {
            return stream
                    .filter(p -> p.getFileName().toString().endsWith(EXTENSION))
                    .sorted()
                    .toArray(Path[]::new);
        }
    }

    // ---------- private AES-GCM helpers ----------

    private static byte[] aesgcmEncrypt(byte[] key, byte[] iv, byte[] plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE,
                new SecretKeySpec(key, "AES"),
                new GCMParameterSpec(GCM_TAG_BITS, iv));
        return cipher.doFinal(plaintext);
    }

    private static byte[] aesgcmDecrypt(byte[] key, byte[] iv, byte[] ciphertext) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE,
                new SecretKeySpec(key, "AES"),
                new GCMParameterSpec(GCM_TAG_BITS, iv));
        return cipher.doFinal(ciphertext);
    }

    /**
     * Resolves the directory next to the running JAR.
     * Falls back to the current working directory when running from an IDE or Maven.
     */
    public static Path defaulPath() {
        try {
            Path location = Path.of(
                    Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()
            );
            if (location.toString().endsWith(".jar")) {
                return location.getParent();
            }
        } catch (Exception ignored) {
            // Ignored
        }
        return Path.of(System.getProperty("user.dir"));
    }
}
