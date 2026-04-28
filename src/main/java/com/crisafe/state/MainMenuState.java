package com.crisafe.state;


import com.crisafe.command.ExitCommand;
import com.crisafe.pattern.Command;
import com.crisafe.pattern.Context;
import com.crisafe.pattern.State;
import com.crisafe.service.BaseService;

import java.util.HashMap;
import java.util.Map;

public class MainMenuState extends BaseService implements State {

    private final Map<String, Command> commands = new HashMap<>();

    public MainMenuState() {
        commands.put("1", context -> context.setState(new OpenArchiveState()));
        commands.put("2", context -> context.setState(new NewArchiveState()));
        commands.put("0", new ExitCommand());
    }

    @Override
    public String display() {
        printBold("=== CriSafe ===");
        print("1) Open existing archive");
        print("2) Create new archive");
        print("Return) Close");
        return readLine("Choice: ");
    }

    @Override
    public void handleInput(String input, Context context) {
        if (input == null || input.isEmpty()) input = "0";
        Command command = commands.get(input);
        if (command != null) {
            command.execute(context);
        } else {
            printRed("Invalid Choice");
        }
    }
}
