package com.crisafe;

import java.io.Console;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class Menu {

    private Scanner scanner;
    private Console console;

    enum OPERATION {
        OPEN_ARCHIVE,
        CREATE_ARCHIVE
    }

    public record Archive(String name, String content, String password, Path file) {
    }

    public record MenuResult(OPERATION operation, Archive archive) {
    }

    private static final String JSON_EXAMPLE = """ 
            {"00": "Example"}
            """;

    public Menu() {
        this.console = System.console();
        this.scanner = console == null ? new Scanner(System.in) : null;
    }

    public MenuResult start(){

        System.out.println("=== CriSafe ===");
        System.out.println("1) Open existing archive");
        System.out.println("2) Create new archive");
        System.out.print("Choice: ");
        String choice = readLine(console, scanner);

        if  (!choice.equals("1") &&  !choice.equals("2")) {
            System.out.println("Invalid choice");
            return start();
        }

        return switch (choice){
            case "1" -> openArchive();
            case "2" -> createArchive();
            default ->  throw new IllegalStateException("Unexpected value: " + choice);
        };

    }

    public MenuResult createArchive() {

        String name = inputName();
        String content = inputContent();
        String password = inputPassword();

        return new MenuResult (OPERATION.CREATE_ARCHIVE, new Archive(name, content, password, null));

    }

    public MenuResult openArchive() {

        Path path = inputArchive();
        String password = inputPassword();

        return new MenuResult (OPERATION.OPEN_ARCHIVE, new Archive(null, null, password, path));

    }

    private Path inputArchive(){

        Path[] files = null;
        try {
            files = FileArchiveService.listArchives();
        } catch (IOException e){
            System.out.println("Error opening archive");
            System.exit(1);
        }

        if (files.length == 0) {
            System.out.println("No " + FileArchiveService.EXTENSION + " archives found in: " + FileArchiveService.defaulPath());
            return inputArchive();
        }

        System.out.println("Available archives:");
        for (int i = 0; i < files.length; i++) {
            System.out.printf("  %d) %s%n", i + 1, files[i].getFileName());
        }
        System.out.print("Select archive number: ");
        String input = readLine(console, scanner);

        int idx = -1;
        try {
            idx = Integer.parseInt(input.trim()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid selection.");
            return inputArchive();
        }
        if (idx < 0 || idx >= files.length) {
            System.out.println("Selection out of range.");
            return inputArchive();
        }

        return files[idx];

    }

    private String inputName() {
        System.out.print("Archive name (without extension): ");
        String name = readLine(console, scanner);
        if (name == null || name.isBlank()) {
            System.out.println("Name cannot be empty.");
            return inputName();
        }
        return name;
    }

    private String inputContent() {
        System.out.print("JSON content (e.g. " + JSON_EXAMPLE +  "): ");
        String json = readLine(console, scanner);
        if (json == null || json.isBlank()) {
            json = JSON_EXAMPLE;
        }
        if (!isValidJsonObject(json)) {
            System.out.println("Invalid input: must be a JSON object starting with '{' and ending with '}'.");
            return inputContent();
        }
        return json;
    }

    private String inputPassword() {
        System.out.print("Password: ");
        String password = readPassword(console, scanner);
        if (password == null || password.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return inputPassword();
        }
        return password;
    }

    private static String readLine(Console console, Scanner scanner) {
        return console != null ? console.readLine() : scanner.nextLine();
    }

    private static String readPassword(Console console, Scanner scanner) {
        return console != null ? new String(console.readPassword()) : scanner.nextLine();
    }

    private static boolean isValidJsonObject(String s) {
        if (s == null) return false;
        String t = s.trim();
        return t.startsWith("{") && t.endsWith("}");
    }


}
