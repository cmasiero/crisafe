package com.crisafe.command;

import com.crisafe.pattern.Command;
import com.crisafe.pattern.Context;
import com.crisafe.service.BaseService;
import com.crisafe.service.FileArchiveService;
import com.crisafe.state.MainMenuState;

import java.nio.file.Path;

public class CreateArchiveCommand extends BaseService implements Command {

    @Override
    public void execute(Context context) {

        String archiveName = context.getAttribute("archiveName");
        String password = context.getAttribute("password");

        if (FileArchiveService.existArchive(archiveName)) {
            printRed("Archive already exists: " + archiveName);
            context.setState(new MainMenuState());
            return;
        }

        Path outputPath = FileArchiveService.defaulPath()
                .resolve(archiveName + FileArchiveService.EXTENSION);
        try {
            fileArchiveService.encrypt("[]", password, outputPath);
            print("Archive created: " + outputPath);
        } catch (Exception e) {
            printRed("Failed to create archive: " + e.getMessage());
        }

        context.setState(new MainMenuState());

    }
}
