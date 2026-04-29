package com.crisafe.state;

import com.crisafe.pattern.Command;
import com.crisafe.pattern.Context;
import com.crisafe.pattern.State;
import com.crisafe.service.OutputService;
import com.crisafe.service.FileArchiveService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class OpenArchiveState implements State {

    private final Map<String, Command> commands = new HashMap<>();
    private final OutputService output = OutputService.getInstance();

    public OpenArchiveState() {
        try {
            Path[] files = FileArchiveService.listArchives();
            for (int i = 0; i < files.length; i++) {
                int finalI = i;
                commands.put(String.valueOf(i + 1), context -> context.setState(new OpenSpecificArchiveState(files[finalI])));
            }
        } catch (IOException e) {
            output.printRed("Error opening archive");
            System.exit(1);
        }
        commands.put("0", context -> context.setState(new MainMenuState()));
    }

    @Override
    public String display() {

        Path[] files = null;
        try {
            files = FileArchiveService.listArchives();
        } catch (IOException e) {
            output.printRed("Error opening archive");
            System.exit(1);
        }

        if (files.length == 0) {
            output.printRed("No " + FileArchiveService.EXTENSION + " archives found in: " + FileArchiveService.defaulPath());
            return null;
        }

        output.printBold("=== Available archives ===");
        for (int i = 0; i < files.length; i++) {
            output.print(String.format("%d) %s", i + 1, files[i].getFileName()));
        }

        output.print("Return) Back");

        return output.readLine("Select archive number: ");

    }

    @Override
    public void handleInput(String input, Context context) {
        if (input == null || input.isEmpty()) input = "0";
        Command command = commands.get(input);
        if (command != null) {
            command.execute(context);
        } else {
            output.printRed("Invalid Choice");
        }

    }

}
