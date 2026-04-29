package com.crisafe.state;

import com.crisafe.pattern.Command;
import com.crisafe.pattern.Context;
import com.crisafe.pattern.State;
import com.crisafe.service.OutputService;

import java.util.HashMap;
import java.util.Map;

public class ArchiveOperationState implements State {

    private final OutputService output = OutputService.getInstance();
    private final Map<String, Command> commands = new HashMap<>();

    public ArchiveOperationState() {
        commands.put("1", context -> context.setState(new FindInArchiveState()));
        commands.put("2", context -> context.setState(new AddInArchiveState()));
        commands.put("3", context -> context.setState(new RemoveFromArchiveState()));
        commands.put("4", context -> context.setState(new ModifyInArchiveState()));
        commands.put("0", context -> context.setState(new OpenArchiveState()));
    }

    @Override
    public String display() {
        output.printBold("=== Archive Operation ===");
        output.print("1) Find In Archive");
        output.print("2) Add In Archive");
        output.print("3) Remove from Archive");
        output.print("4) Modify in Archive");
        output.print("Return) Back");
        return output.readLine("Choice: ");
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
