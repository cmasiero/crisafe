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

import java.util.regex.Pattern;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MenuJLine {

    private static final String JSON_EXAMPLE = """
            {"00": "Example"}
            """;

    private final Terminal terminal;
    private final LineReader reader;

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
    }

    public MenuResult start() {

        printDefaultColor("=== CriSafe ===");
        printDefaultColor("1) Open existing archive");
        printDefaultColor("2) Create new archive");
        printDefaultColor("1000) Close");

        String choice = readLine("Choice: ");

        if  (choice.equals("1000")) {
            System.exit(0);
        }

        return switch (choice) {
            case "1" -> openArchive();
            case "2" -> createArchive();
            default -> {
                printRed("Invalid choice: " +  choice);
                yield start();
            }
        };
    }

    public MenuResult createArchive() {
        String name = inputName();
        String content = createContent();
        String password = inputPassword();
        return new MenuResult(Operation.CREATE_ARCHIVE, new Archive(name, content, password, null));
    }

    public MenuResult openArchive() {
        Path path = inputArchive();
        if (path == null){
            start();
        }
        String password = inputPassword();
        return new MenuResult(Operation.OPEN_ARCHIVE, new Archive(null, null, password, path));
    }

    private Path inputArchive() {

        Path[] files = null;
        try {
            files = FileArchiveService.listArchives();
        } catch (IOException e) {
            terminal.writer().println("Error opening archive");
            terminal.writer().flush();
            System.exit(1);
        }

        if (files.length == 0) {
            terminal.writer().println("No " + FileArchiveService.EXTENSION + " archives found in: " + FileArchiveService.defaulPath());
            terminal.writer().flush();
            return null;
        }

        terminal.writer().println("Available archives:");
        for (int i = 0; i < files.length; i++) {
            terminal.writer().printf("  %d) %s%n", i + 1, files[i].getFileName());
        }
        terminal.writer().flush();

        String input = readLine("Select archive number: ");

        int idx = -1;
        try {
            idx = Integer.parseInt(input.trim()) - 1;
        } catch (NumberFormatException e) {
            terminal.writer().println("Invalid selection.");
            terminal.writer().flush();
            return inputArchive();
        }

        if (idx < 0 || idx >= files.length) {
            terminal.writer().println("Selection out of range.");
            terminal.writer().flush();
            return inputArchive();
        }

        return files[idx];
    }

    private String inputName() {
        String name = readLine("Archive name (without extension): ");
        if (name == null || name.isBlank()) {
            terminal.writer().println("Name cannot be empty.");
            terminal.writer().flush();
            return inputName();
        }
        return name;
    }

    private String createContent() {
        terminal.writer().println("Create new archive content: " + JSON_EXAMPLE);
        return JSON_EXAMPLE;
    }

    private String inputPassword() {
        String password = readPassword("Password: ");
        if (password == null || password.isEmpty()) {
            terminal.writer().println("Password cannot be empty.");
            terminal.writer().flush();
            return inputPassword();
        }
        return password;
    }

    private String readLine(String prompt) {
        try {
            return reader.readLine(prompt);
        } catch (UserInterruptException | EndOfFileException e) {
            terminal.writer().println("\nAborted.");
            terminal.writer().flush();
            System.exit(0);
            return null;
        }
    }

    private String readPassword(String prompt) {
        try {
            return reader.readLine(prompt, '*');
        } catch (UserInterruptException | EndOfFileException e) {
            terminal.writer().println("\nAborted.");
            terminal.writer().flush();
            System.exit(0);
            return null;
        }
    }

    public void printDefaultColor(String message){
        new AttributedStringBuilder()
                .style(AttributedStyle.DEFAULT)
                .append(message)
                .toAttributedString()
                .println(terminal);
        terminal.flush();
    }

    public void printRed (String message){
        new AttributedStringBuilder()
                .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
                .append(message)
                .toAttributedString()
                .println(terminal);
        terminal.flush();
    }

    public void close() throws IOException {
        terminal.close();
    }
}
