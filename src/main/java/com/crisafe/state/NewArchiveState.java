package com.crisafe.state;

import com.crisafe.command.CreateArchiveCommand;
import com.crisafe.pattern.Command;
import com.crisafe.pattern.Context;
import com.crisafe.pattern.State;
import com.crisafe.service.OutputService;

import static com.crisafe.util.Constant.FIELD_SEP;

public class NewArchiveState implements State {

    private final OutputService output = OutputService.getInstance();
    private final Command createArchiveCommand = new CreateArchiveCommand();

    @Override
    public String display() {
        output.printBold("=== New archive ===");
        String archiveName = output.readLine("Archive name: ");
        String password    = output.readPassword("Archive password: ");
        return archiveName + FIELD_SEP + password;
    }

    @Override
    public void handleInput(String input, Context context) {
        String[] parts     = input.split(FIELD_SEP, 2);
        String archiveName = parts[0];
        String password    = parts[1];

        context.setAttribute("archiveName", archiveName);
        context.setAttribute("password", password);

        createArchiveCommand.execute(context);
    }
}
