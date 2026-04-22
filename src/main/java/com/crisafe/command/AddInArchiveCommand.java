package com.crisafe.command;

import com.crisafe.pattern.Command;
import com.crisafe.pattern.Context;
import com.crisafe.service.BaseService;

public class AddInArchiveCommand extends BaseService implements Command {

    private String json;

    public AddInArchiveCommand(String json) {
        this.json = json;
    }

    @Override
    public void execute(Context context) {




    }

}
