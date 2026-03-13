package com.crisafe;

import java.io.Console;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    private static final String JSON_EXAMPLE = """ 
            {"00": "Example"}
            """;

    public static void main(String[] args) throws Exception {
        Console console = System.console();
        Scanner scanner = console == null ? new Scanner(System.in) : null;

        CryptoService      crypto  = new CryptoService();
        FileArchiveService archive = new FileArchiveService(crypto);
        Path               jarDir  = resolveJarDirectory();

        System.out.println("=== CriSafe ===");
        System.out.println("1) Create new archive");
        System.out.println("2) Open existing archive");
        System.out.print("Choice: ");
        String choice = readLine(console, scanner);

        if ("1".equals(choice)) {
            createArchive(console, scanner, archive, jarDir);
        } else if ("2".equals(choice)) {
            openArchive(console, scanner, archive, jarDir);
        } else {
            System.out.println("Invalid choice.");
        }
    }

    // ---------- menu actions ----------

    private static void createArchive(Console console, Scanner scanner,
                                      FileArchiveService archive, Path jarDir) throws Exception {
        System.out.print("Archive name (without extension): ");
        String name = readLine(console, scanner);
        if (name == null || name.isBlank()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        System.out.print("JSON content (e.g. " + JSON_EXAMPLE +  "): ");
        String json = readLine(console, scanner);
        if (json == null || json.isBlank()) {
            json = JSON_EXAMPLE;
        }
        if (!isValidJsonObject(json)) {
            System.out.println("Invalid input: must be a JSON object starting with '{' and ending with '}'.");
            return;
        }

        System.out.print("Password: ");
        String password = readPassword(console, scanner);
        if (password == null || password.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return;
        }

        Path outputPath = jarDir.resolve(name + FileArchiveService.EXTENSION);
        archive.encrypt(json, password, outputPath);
        System.out.println("Archive created: " + outputPath);
    }

    private static void openArchive(Console console, Scanner scanner,
                                    FileArchiveService archive, Path jarDir) throws Exception {
        Path[] files = FileArchiveService.listArchives(jarDir);
        if (files.length == 0) {
            System.out.println("No " + FileArchiveService.EXTENSION + " archives found in: " + jarDir);
            return;
        }

        System.out.println("Available archives:");
        for (int i = 0; i < files.length; i++) {
            System.out.printf("  %d) %s%n", i + 1, files[i].getFileName());
        }
        System.out.print("Select archive number: ");
        String input = readLine(console, scanner);

        int idx;
        try {
            idx = Integer.parseInt(input.trim()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid selection.");
            return;
        }
        if (idx < 0 || idx >= files.length) {
            System.out.println("Selection out of range.");
            return;
        }

        System.out.print("Password: ");
        String password = readPassword(console, scanner);

        try {
            String json = archive.decrypt(files[idx], password);
            System.out.println("Content: " + json);
        } catch (Exception e) {
            System.out.println("Failed to decrypt: wrong password or corrupted file.");
        }
    }

    // ---------- helpers ----------

    /**
     * Resolves the directory next to the running JAR.
     * Falls back to the current working directory when running from an IDE or Maven.
     */
    private static Path resolveJarDirectory() {
        try {
            Path location = Path.of(
                    Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()
            );
            if (location.toString().endsWith(".jar")) {
                return location.getParent();
            }
        } catch (Exception ignored) {}
        return Path.of(System.getProperty("user.dir"));
    }

    private static boolean isValidJsonObject(String s) {
        if (s == null) return false;
        String t = s.trim();
        return t.startsWith("{") && t.endsWith("}");
    }

    private static String readLine(Console console, Scanner scanner) {
//        System.out.println("-->" + console.readLine());
        return console != null ? console.readLine() : scanner.nextLine();
    }

    private static String readPassword(Console console, Scanner scanner) {
        return console != null ? new String(console.readPassword()) : scanner.nextLine();
    }
}
