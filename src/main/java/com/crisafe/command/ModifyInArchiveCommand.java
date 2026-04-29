package com.crisafe.command;

import com.crisafe.ArchiveRecord;
import com.crisafe.pattern.Command;
import com.crisafe.pattern.Context;
import com.crisafe.service.FileArchiveService;
import com.crisafe.service.OutputService;
import com.crisafe.state.ArchiveOperationState;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.nio.file.Path;
import java.util.List;

public class ModifyInArchiveCommand implements Command {

    private final OutputService output = OutputService.getInstance();
    private final FileArchiveService fileArchive = FileArchiveService.getInstance();

    @Override
    public void execute(Context context) {

        ArchiveRecord oldRecord = new ArchiveRecord(
                context.getAttribute("modify.old.company"),
                context.getAttribute("modify.old.user"),
                context.getAttribute("modify.old.pass"),
                context.getAttribute("modify.old.note")
        );
        ArchiveRecord newRecord = new ArchiveRecord(
                context.getAttribute("modify.new.company"),
                context.getAttribute("modify.new.user"),
                context.getAttribute("modify.new.pass"),
                context.getAttribute("modify.new.note")
        );

        String json = context.getAttribute("json");
        Gson gson = new Gson();
        List<ArchiveRecord> records = gson.fromJson(json, new TypeToken<List<ArchiveRecord>>() {}.getType());

        int idx = records.indexOf(oldRecord);
        if (idx < 0) {
            output.printRed("Record no longer exists in archive");
            context.setState(new ArchiveOperationState());
            return;
        }

        records.set(idx, newRecord);
        String newJson = gson.toJson(records);

        try {
            fileArchive.encrypt(newJson, context.getAttribute("archivePassword"),
                    Path.of(context.getAttribute("archivePath")));
            context.setAttribute("json", newJson);
            output.printGreen("Record updated");
        } catch (Exception e) {
            output.printRed("Failed to save: " + e.getMessage());
        }

        context.setState(new ArchiveOperationState());
    }

}
