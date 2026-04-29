package com.crisafe.command;

import com.crisafe.ArchiveRecord;
import com.crisafe.pattern.Command;
import com.crisafe.pattern.Context;
import com.crisafe.service.OutputService;
import com.crisafe.service.FileArchiveService;
import com.crisafe.state.ArchiveOperationState;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.nio.file.Path;
import java.util.List;

public class RemoveFromArchiveCommand implements Command {

    private final OutputService output = OutputService.getInstance();
    private final FileArchiveService fileArchive = FileArchiveService.getInstance();

    @Override
    public void execute(Context context) {

        String company  = context.getAttribute("remove.company");
        String user     = context.getAttribute("remove.user");
        String pass     = context.getAttribute("remove.pass");
        String note     = context.getAttribute("remove.note");
        String json     = context.getAttribute("json");

        Gson gson = new Gson();
        List<ArchiveRecord> records = gson.fromJson(json, new TypeToken<List<ArchiveRecord>>() {}.getType());
        records.remove(new ArchiveRecord(company, user, pass, note));
        String newJson = gson.toJson(records);

        try {
            fileArchive.encrypt(newJson, context.getAttribute("archivePassword"),
                    Path.of(context.getAttribute("archivePath")));
            context.setAttribute("json", newJson);
            output.printGreen("Record removed");
        } catch (Exception e) {
            output.printRed("Failed to save: " + e.getMessage());
        }

        context.setState(new ArchiveOperationState());

    }

}
