package com.crisafe;

public class ExitCommand extends BaseOutput implements Command {

    public void execute(Context context) {
        printRed("Exit...");
        context.stop();
    }

}