package com.crisafe.service;

import org.jline.reader.*;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class BaseService {

    public final FileArchiveService fileArchiveService;

    private final Terminal terminal;
    private final LineReader reader;
    private final LineReader passwordReader;

    public BaseService() {

        CryptoService crypto = new CryptoService();
        fileArchiveService = new FileArchiveService(crypto);

        Logger.getLogger("org.jline").setLevel(Level.SEVERE);
        try {
            this.terminal = TerminalBuilder.builder().system(true).build();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize terminal", e);
        }
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

    public void print(String message){
        print(message, AttributedStyle.DEFAULT);
    }

    public void printBold(String message){
        print(message, AttributedStyle.BOLD);
    }

    public void printGreen (String message){
        print(message, AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));
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

    public String readPassword(String prompt) {
        try {
            return passwordReader.readLine(prompt, '*');
        } catch (UserInterruptException | EndOfFileException e) {
            printRed("\nAborted.");
            System.exit(0);
            return null;
        }
    }

    public void close() throws IOException {
        terminal.close();
    }

    public String readLine(String prompt) {
        try {
            return reader.readLine(prompt);
        } catch (UserInterruptException | EndOfFileException e) {
            printRed("\nAborted.");
            System.exit(0);
            return null;
        }
    }

}
