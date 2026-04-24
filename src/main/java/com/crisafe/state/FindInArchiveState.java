package com.crisafe.state;

import com.crisafe.command.FindInArchiveCommand;
import com.crisafe.pattern.Command;
import com.crisafe.pattern.Context;
import com.crisafe.pattern.State;
import com.crisafe.service.BaseService;

public class FindInArchiveState extends BaseService implements State {

    private final Command command = new FindInArchiveCommand();

    @Override
    public String display() {
        printBold("=== Find In Archive ===");
        return readLine("Search for: ");
    }

    @Override
    public void handleInput(String input, Context context) {

        if (input == null || input.isEmpty()) {
            context.setState(new ArchiveOperationState());
            return;
        }

        context.setAttribute("search", input);
        command.execute(context);

    }

}
