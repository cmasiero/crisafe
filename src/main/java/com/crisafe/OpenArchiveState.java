package com.crisafe;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class OpenArchiveState extends BaseOutput implements State {

    private Map<String, Command> commands = new HashMap<>();

    public OpenArchiveState() {
        try {
            Path[] files = FileArchiveService.listArchives();
            for (int i = 0; i < files.length; i++) {
                int finalI = i;
                commands.put(String.valueOf(i + 1), context -> context.setState(new OpenSpecificArchiveState(files[finalI])));
            }
        } catch (IOException e) {
            printRed("Error opening archive");
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
            printRed("Error opening archive");
            System.exit(1);
        }

        if (files.length == 0) {
            printRed("No " + FileArchiveService.EXTENSION + " archives found in: " + FileArchiveService.defaulPath());
            return null;
        }

        printBold("Available archives:");
        for (int i = 0; i < files.length; i++) {
            print(String.format("%d) %s", i + 1, files[i].getFileName()));
        }

        print("0) Back");

        return readLine("Select archive number: ");

    }

    @Override
    public void handleInput(String input, Context context) {

        Command command = commands.get(input);
        if (command != null) {
            command.execute(context);
        } else {
            printRed("Invalid Choice");
        }

    }

}
