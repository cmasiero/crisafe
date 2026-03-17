package com.crisafe;

import java.nio.file.Path;

public class Main {

    CryptoService crypto;
    FileArchiveService archive;

    public Main() {

        crypto = new CryptoService();
        archive = new FileArchiveService(crypto);

    }

    public static void main(String[] args) throws Exception {

        Main m = new Main();
        m.init();

    }

    public void init() throws Exception {

        Menu menu = new Menu();
        Menu.MenuResult result = menu.start();
        operation(result);

    }

    private void operation(Menu.MenuResult result) throws Exception{

        switch (result.operation()) {
            case OPEN_ARCHIVE -> {
                System.out.println("Opening archive");
                Menu.Archive archiveTmp = result.archive();
                String json = null;
                try {
                    json = archive.decrypt(archiveTmp.file(), archiveTmp.password());
                } catch (Exception e) {
                    System.out.println("the password is wrong or the file is corrupted");
                    Menu menu = new Menu();
                    operation(menu.openArchive());
                    return;
                }
                System.out.println("Content: " + json);
            }
            case CREATE_ARCHIVE -> {
                Menu.Archive archiveTmp = result.archive();
                Path outputPath = FileArchiveService.defaulPath().resolve(archiveTmp.name() + FileArchiveService.EXTENSION);
                archive.encrypt(archiveTmp.content(), archiveTmp.password(), outputPath);
                System.out.println("Archive created: " + outputPath);
                init();
            }
        }

    }

}
