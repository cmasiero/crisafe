package com.crisafe.command;

import com.crisafe.pattern.Command;
import com.crisafe.pattern.Context;
import com.crisafe.service.OutputService;
import com.crisafe.service.FileArchiveService;
import com.crisafe.state.MainMenuState;

import java.nio.file.Path;

public class CreateArchiveCommand implements Command {

    private final OutputService output = OutputService.getInstance();
    private final FileArchiveService fileArchive = FileArchiveService.getInstance();

    @Override
    public void execute(Context context) {

        String archiveName = context.getAttribute("archiveName");
        String password = context.getAttribute("password");

        if (FileArchiveService.existArchive(archiveName)) {
            output.printRed("Archive already exists: " + archiveName);
            context.setState(new MainMenuState());
            return;
        }

        Path outputPath = FileArchiveService.defaulPath()
                .resolve(archiveName + FileArchiveService.EXTENSION);
        try {
            fileArchive.encrypt("[]", password, outputPath);
            output.print("Archive created: " + outputPath);
        } catch (Exception e) {
            output.printRed("Failed to create archive: " + e.getMessage());
        }

        context.setState(new MainMenuState());

    }
}
