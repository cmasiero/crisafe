package com.crisafe.state;

import com.crisafe.command.FindInArchiveCommand;
import com.crisafe.pattern.Command;
import com.crisafe.pattern.Context;
import com.crisafe.pattern.State;
import com.crisafe.service.OutputService;

public class FindInArchiveState implements State {

    private final OutputService output = OutputService.getInstance();
    private final Command command = new FindInArchiveCommand();

    @Override
    public String display() {
        output.printBold("=== Find In Archive, '*' for all. Return to go back. ===");
        return output.readLine("Search for: ");
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
