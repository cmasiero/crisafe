package com.crisafe.state;

import com.crisafe.command.AddInArchiveCommand;
import com.crisafe.pattern.Command;
import com.crisafe.pattern.Context;
import com.crisafe.pattern.State;
import com.crisafe.service.OutputService;

import static com.crisafe.util.Constant.FIELD_SEP;

public class AddInArchiveState implements State {

    private final OutputService output = OutputService.getInstance();
    private final Command command = new AddInArchiveCommand();

    @Override
    public String display() {
        output.printBold("=== Add in Archive, always Return to go back. ===");
        String company  = output.readLine("Company: ");
        String user     = output.readLine("User: ");
        String password = output.readLine("Password: ");
        String note     = output.readLine("Note: ");
        return company + FIELD_SEP + user + FIELD_SEP + password + FIELD_SEP + note;
    }

    @Override
    public void handleInput(String input, Context context) {

        String[] parts = input.split(FIELD_SEP, 4);
        context.setAttribute("archive.company", parts[0]);
        context.setAttribute("archive.user", parts[1]);
        context.setAttribute("archive.password", parts[2]);
        context.setAttribute("archive.note", parts[3]);

        if ("".equals(parts[0]) && "".equals(parts[1]) &&
                "".equals(parts[2]) && "".equals(parts[3])) {
            output.printRed("No records Added, Go Back!");
            context.setState(new ArchiveOperationState());
            return;
        }

        command.execute(context);

    }

}
