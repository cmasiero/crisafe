package com.crisafe.state;

import com.crisafe.command.CreateArchiveCommand;
import com.crisafe.pattern.Command;
import com.crisafe.pattern.Context;
import com.crisafe.pattern.State;
import com.crisafe.service.BaseService;
import static com.crisafe.util.Constant.FIELD_SEP;


public class NewArchiveState extends BaseService implements State {

    private final Command createArchiveCommand = new CreateArchiveCommand();

    @Override
    public String display() {
        printBold("=== New archive ===");
        String archiveName = readLine("Archive name: ");
        String password    = readPassword("Archive password: ");
        return archiveName + FIELD_SEP + password;
    }

    @Override
    public void handleInput(String input, Context context) {
        String[] parts    = input.split(FIELD_SEP, 2);
        String archiveName = parts[0];
        String password    = parts[1];

        context.setAttribute("archiveName", archiveName);
        context.setAttribute("password", password);

        createArchiveCommand.execute(context);
    }
}
