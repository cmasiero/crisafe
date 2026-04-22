package com.crisafe.state;

import com.crisafe.command.AddInArchiveCommand;
import com.crisafe.pattern.Command;
import com.crisafe.command.FindInArchiveCommand;
import com.crisafe.pattern.Context;
import com.crisafe.pattern.State;
import com.crisafe.service.BaseService;

import java.util.HashMap;
import java.util.Map;

public class ArchiveOperationState extends BaseService implements State {

    private Map<String, Command> commands = new HashMap<>();


    public ArchiveOperationState() {
        commands.put("1", new FindInArchiveCommand());
        commands.put("2", context -> context.setState(new AddInArchiveState()));
        commands.put("0", context -> context.setState(new OpenArchiveState()));

    }

    @Override
    public String display() {
        printBold("=== Archive Operation ===");
        print("1) Find In Archive");
        print("2) Add In Archive");
        print("0) Back");
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
