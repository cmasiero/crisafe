package com.crisafe;

import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.EndOfFileException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.util.Optional;
import java.util.regex.Pattern;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MenuJLine {

    private final Terminal terminal;
    private final LineReader reader;
    private final LineReader passwordReader;
    private MenuResult currentResult;

    public MenuJLine() throws IOException {
        Logger.getLogger("org.jline").setLevel(Level.SEVERE);
        this.terminal = TerminalBuilder.builder().system(true).build();
        this.reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .highlighter(new Highlighter() {
                    @Override
                    public AttributedString highlight(LineReader r, String buffer) {
                        return new AttributedStringBuilder()
                                .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN))
                                .append(buffer)
                                .toAttributedString();
                    }
                    @Override public void setErrorPattern(Pattern p) {
                        throw new UnsupportedOperationException("notImplemented() cannot be performed because ...");
                    }
                    @Override public void setErrorIndex(int i) {
                        throw new UnsupportedOperationException("notImplemented() cannot be performed because ...");
                    }
                })
                .build();
        this.passwordReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();
    }

    public MenuResult start() {

        printBold("=== CriSafe ===");
        print("1) Open existing archive");
        print("2) Create new archive");
        print("999) Close");

        String choice = readLine("Choice: ");

        if (choice.equals("999")) {
            try {
                close();
            } catch (IOException e) {
                printRed("Error closing terminal: " + e.getMessage());
            }
            System.exit(0);
        }

        currentResult = switch (choice) {
            case "1" -> openArchive();
            case "2" -> createArchive();
            default -> {
                printRed("Invalid choice: " +  choice);
                yield start();
            }
        };

        return currentResult;
    }

    public MenuResult createArchive() {
        printBold("=== Creating archive ===");
        String name = inputName();
        String content = createContent();
        String password = inputPassword();
        currentResult = new MenuResult(Operation.CREATE_ARCHIVE, new Archive(name, content, password, null), Optional.empty());
        return currentResult;
    }

    public MenuResult openArchive() {
        Path path = inputArchive();
        if (path == null){
            return new MenuResult(Operation.RESTART, null, Optional.empty());
        }
        String password = inputPassword();
        currentResult = new MenuResult(Operation.OPEN_ARCHIVE, new Archive(null, null, password, path), Optional.empty());
        return currentResult;
    }

    public MenuResult operationInArchive(){

        printBold("=== Operation in archive ===");
        print("1) Filter");
        print("2) Create record");
        print("999) Back");

        String choice = readLine("Choice: ");

        if ("999".equals(choice)) {
            return openArchive();
        } else if ("1".equals(choice)) {
            currentResult = new MenuResult(Operation.FILTER_RECORD, currentResult.archive(), Optional.empty());
            return currentResult;
        } else if ("2".equals(choice)) {
            currentResult = new MenuResult(Operation.CREATE_RECORD, currentResult.archive(), Optional.empty());
            return currentResult;
        } else {
            printRed("Invalid choice: " +  choice);
            return operationInArchive();
        }

    }

    public MenuResult findInArchive(){

        printBold("=== Find in archive ===");
        String filter = readLine("Filter (leave empty to show all): ");

        currentResult = new MenuResult(Operation.FILTER_RECORD,currentResult.archive(), Optional.of(filter));

        return currentResult;

    }

    private Path inputArchive() {

        Path[] files = null;
        try {
            files = FileArchiveService.listArchives();
        } catch (IOException e) {
            printRed("Error opening archive");
            System.exit(1);
        }

        if (files.length == 0) {
            printRed("No " + FileArchiveService.EXTENSION + " archives found in: " + FileArchiveService.defaulPath());
            return null;
        }

        printBold("Available archives:");
        for (int i = 0; i < files.length; i++) {
            print(String.format("  %d) %s", i + 1, files[i].getFileName()));
        }
        print("  999) Back");

        String input = readLine("Select archive number: ");

        if (input.equals("999")) {
            return null;
        }

        int idx = -1;
        try {
            idx = Integer.parseInt(input.trim()) - 1;
        } catch (NumberFormatException e) {
            printRed("Invalid selection.");
            return inputArchive();
        }

        if (idx < 0 || idx >= files.length) {
            printRed("Selection out of range.");
            return inputArchive();
        }

        return files[idx];
    }

    private String inputName() {
        String name = readLine("Archive name (without extension): ");

        if (name == null || name.isBlank()) {
            printRed("Name cannot be empty.");
            return inputName();
        }

        if (FileArchiveService.existArchive(name)) {
            printRed("File already exists.");
            return inputName();
        }
        return name;
    }

    private String createContent() {
        ArchiveRecord empty = new ArchiveRecord("", "", "", "");
        return "[" + empty.toJson() + "]";
    }

    private String inputPassword() {
        String password = readPassword("Password: ");
        if (password == null || password.isEmpty()) {
            printRed("Password cannot be empty.");
            return inputPassword();
        }
        return password;
    }

    private String readLine(String prompt) {
        try {
            return reader.readLine(prompt);
        } catch (UserInterruptException | EndOfFileException e) {
            printRed("\nAborted.");
            System.exit(0);
            return null;
        }
    }

    private String readPassword(String prompt) {
        try {
            return passwordReader.readLine(prompt, '*');
        } catch (UserInterruptException | EndOfFileException e) {
            printRed("\nAborted.");
            System.exit(0);
            return null;
        }
    }

    public void print(String message){
        print(message, AttributedStyle.DEFAULT);
    }

    public void printBold(String message){
        print(message, AttributedStyle.BOLD);
    }

    public void printRed (String message){
        print(message, AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
    }

    private void print (String message, AttributedStyle style){
        new AttributedStringBuilder()
                .style(style)
                .append(message)
                .toAttributedString()
                .println(terminal);
        terminal.flush();
    }

    public void close() throws IOException {
        terminal.close();
    }
}
