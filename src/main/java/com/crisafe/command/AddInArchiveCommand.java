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

public class AddInArchiveCommand implements Command {

    private final OutputService output = OutputService.getInstance();
    private final FileArchiveService fileArchive = FileArchiveService.getInstance();

    @Override
    public void execute(Context context) {

        String company  = context.getAttribute("archive.company");
        String user     = context.getAttribute("archive.user");
        String password = context.getAttribute("archive.password");
        String note     = context.getAttribute("archive.note");
        String json     = context.getAttribute("json");

        Gson gson = new Gson();
        List<ArchiveRecord> records = gson.fromJson(json, new TypeToken<List<ArchiveRecord>>() {}.getType());
        records.add(new ArchiveRecord(company, user, password, note));
        String newJson = gson.toJson(records);

        try {
            fileArchive.encrypt(newJson, context.getAttribute("archivePassword"),
                    Path.of(context.getAttribute("archivePath")));
            context.setAttribute("json", newJson);
            output.printGreen("Records added");
        } catch (Exception e) {
            output.printRed("Failed to save: " + e.getMessage());
        }

        context.setState(new ArchiveOperationState());

    }

}
