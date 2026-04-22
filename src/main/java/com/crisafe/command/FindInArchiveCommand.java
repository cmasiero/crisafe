package com.crisafe.command;

import com.crisafe.service.BaseService;
import com.crisafe.pattern.Command;
import com.crisafe.pattern.Context;

public class FindInArchiveCommand extends BaseService implements Command {
    @Override
    public void execute(Context context) {
            printRed("Find in archive...");
    }
}
