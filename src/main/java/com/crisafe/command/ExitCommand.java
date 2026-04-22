package com.crisafe.command;

import com.crisafe.service.BaseService;
import com.crisafe.pattern.Command;
import com.crisafe.pattern.Context;

public class ExitCommand extends BaseService implements Command {

    public void execute(Context context) {
        printRed("Exit...");
        context.stop();
    }

}