package com.crisafe.state;

import com.crisafe.pattern.Context;
import com.crisafe.pattern.State;
import com.crisafe.service.OutputService;
import com.crisafe.service.FileArchiveService;

import java.nio.file.Path;

public class OpenSpecificArchiveState implements State {

    private final OutputService output = OutputService.getInstance();
    private final FileArchiveService fileArchive = FileArchiveService.getInstance();
    public final Path path;

    public OpenSpecificArchiveState(Path path) {
        this.path = path;
    }

    @Override
    public String display() {
        return output.readPassword("Enter password for " + path.getFileName() + ": ");
    }

    @Override
    public void handleInput(String password, Context context) {

        try {
            String json = fileArchive.decrypt(path, password);
            context.setAttribute("json", json);
            context.setAttribute("archivePath", path.toString());
            context.setAttribute("archivePassword", password);
            context.setState(new ArchiveOperationState());
        } catch (Exception e) {
            output.printRed("The password is wrong or the file is corrupted");
            context.setState(new OpenSpecificArchiveState(path));
        }

    }

}
