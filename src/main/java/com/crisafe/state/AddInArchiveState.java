package com.crisafe.state;

import com.crisafe.command.AddInArchiveCommand;
import com.crisafe.pattern.Command;
import com.crisafe.pattern.Context;
import com.crisafe.pattern.State;
import com.crisafe.service.BaseService;

import static com.crisafe.util.Constant.FIELD_SEP;

public class AddInArchiveState extends BaseService implements State {

    private final Command command = new AddInArchiveCommand();

    @Override
    public String display() {
        printBold("=== Add in Archive ===");
        String company = readLine("Company: ");
        String user    = readPassword("User: ");
        String password = readPassword("Password: ");
        String note    = readLine("Note: ");
        return company + FIELD_SEP + user + FIELD_SEP + password + FIELD_SEP + note;
    }

    @Override
    public void handleInput(String input, Context context) {

        String[] parts = input.split(FIELD_SEP, 4);
        context.setAttribute("archive.company", parts[0]);
        context.setAttribute("archive.user", parts[1]);
        context.setAttribute("archive.password", parts[2]);
        context.setAttribute("archive.note", parts[3]);



        command.execute(context);

    }

}
