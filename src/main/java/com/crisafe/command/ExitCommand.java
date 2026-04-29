package com.crisafe.command;

import com.crisafe.service.OutputService;
import com.crisafe.pattern.Command;
import com.crisafe.pattern.Context;

public class ExitCommand implements Command {

    private final OutputService output = OutputService.getInstance();

    public void execute(Context context) {
        output.printRed("Exit...");
        context.stop();
    }

}
