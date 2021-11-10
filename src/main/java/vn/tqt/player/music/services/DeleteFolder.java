package vn.tqt.player.music.services;

import vn.tqt.player.music.initWindow.InitAlertWindow;

import java.io.File;

public class DeleteFolder {
    public static void delete(String folderPath) {
        File myFolder = new File(folderPath);
        String[]entries = myFolder.list();
        for(String s: entries){
            File currentFile = new File(myFolder.getPath(),s);
            currentFile.delete();
        }
        myFolder.delete();
        String tileAlert = "Player Alert!!";
        String contentAlert = "Delete\"" + myFolder.getName() + "\" Completed!";
        InitAlertWindow.initAlert(tileAlert, contentAlert);
    }
}
