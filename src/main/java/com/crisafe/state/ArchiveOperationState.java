package com.crisafe.state;

import com.crisafe.pattern.Command;
import com.crisafe.command.FindInArchiveCommand;
import com.crisafe.pattern.Context;
import com.crisafe.pattern.State;
import com.crisafe.service.BaseService;

import java.util.HashMap;
import java.util.Map;

public class ArchiveOperationState extends BaseService implements State {

    private String json ;
    private Map<String, Command> commands = new HashMap<>();


    public ArchiveOperationState(String json) {
        this.json = json;
        commands.put("1", new FindInArchiveCommand());
//        commands.put("2", new AddInArchiveCommand()));
//        commands.put("3", new ModifyInArchiveCommand()));
//        commands.put("4", new RemoveFromArchiveCommand()));
        commands.put("0", context -> context.setState(new OpenArchiveState()));

    }

    @Override
    public String display() {
        System.out.println(json);
        printBold("=== Archive Operation ===");
        print("1) Find In Archive");
//        print("2) Create new archive");
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
