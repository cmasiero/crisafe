package com.crisafe.state;

import com.crisafe.service.BaseService;
import com.crisafe.pattern.Context;
import com.crisafe.pattern.State;

import java.nio.file.Path;

public class OpenSpecificArchiveState extends BaseService implements State {

    public final Path path;

    public OpenSpecificArchiveState(Path path) {
        this.path = path;
    }

    @Override
    public String display() {
        return readPassword("Enter password for " + path.getFileName() + ": ");
    }

    @Override
    public void handleInput(String password, Context context) {

        try {
            String json = archive.decrypt(path, password);
            context.setState(new ArchiveOperationState(json));
        } catch (Exception e) {
            printRed("The password is wrong or the file is corrupted");
            context.setState(new OpenSpecificArchiveState(path));
        }

    }

}
