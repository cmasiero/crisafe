package com.crisafe;


import java.util.HashMap;
import java.util.Map;

public class MainMenuState extends BaseOutput implements State {

    private Map<String, Command> commands = new HashMap<>();

    public MainMenuState() {
        commands.put("1", context -> context.setState(new OpenArchiveState()));
        commands.put("0", new ExitCommand());
    }

    @Override
    public String display() {
        printBold("=== CriSafe ===");
        print("1) Open existing archive");
        print("2) Create new archive");
        print("0) Close");
        return readLine("Choice: ");
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
