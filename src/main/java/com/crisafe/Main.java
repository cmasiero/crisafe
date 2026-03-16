package com.crisafe;

import java.nio.file.Path;

public class Main {

    CryptoService crypto;
    FileArchiveService archive;

    public Main(){
        crypto = new CryptoService();
        archive = new FileArchiveService(crypto);
    }

    public static void main(String[] args) throws Exception {

        Main m = new Main();
        m.init();

    }

    public void init() throws Exception{

        Menu menu = new Menu();
        Menu.MenuResult result = menu.start();

        switch (result.operation()) {
            case OPEN_ARCHIVE -> {
                System.out.println("Opening archive");
                //        try {
//            String json = archive.decrypt(files[idx], password);
//            System.out.println("Content: " + json);
//        } catch (Exception e) {
//            System.out.println("Failed to decrypt: wrong password or corrupted file.");
//        }
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
