package com.crisafe;

import java.nio.file.Path;

public class Main {

    CryptoService crypto;
    FileArchiveService archive;
    ArchiveManager archiveManager;
    MenuJLine menu;

    public Main() {
        crypto = new CryptoService();
        archive = new FileArchiveService(crypto);
    }

    public static void main(String[] args) throws Exception {

        Main m = new Main();
        m.init();

    }

    public void init() throws Exception {

        menu = new MenuJLine();
        MenuResult result = menu.start();
        operation(result);

    }

    private void operation(MenuResult result) throws Exception{

        switch (result.operation()) {
            case CREATE_ARCHIVE -> {
                Archive archiveTmp = result.archive();
                Path outputPath = FileArchiveService.defaulPath().resolve(archiveTmp.name() + FileArchiveService.EXTENSION);
                archive.encrypt(archiveTmp.content(), archiveTmp.password(), outputPath);
                menu.printRed("Archive created: " + outputPath);
                init();
            }
            case OPEN_ARCHIVE -> {
                Archive archiveTmp = result.archive();
                String json = null;
                try {
                    json = archive.decrypt(archiveTmp.file(), archiveTmp.password());
                } catch (Exception e) {
                    menu.printRed("The password is wrong or the file is corrupted");
                    operation(menu.openArchive());
                    return;
                }
                archiveManager = new ArchiveManager(json);
                operation(menu.operationInArchive());
            }
            case FILTER_RECORD -> {
                System.out.println("#### FILTER_RECORD ####");
            }
            case CREATE_RECORD -> {
                System.out.println("#### CREATE_RECORD ####");
            }
            case RESTART -> init();
        }

    }

}
