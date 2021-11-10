package vn.tqt.player.music.services;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class CopyFile {
    public static void copyMusicToDirectory(String songPath, String playlistName) {
        File source = new File(songPath);
        File dest = new File("playlist/" + playlistName);
        try {
            FileUtils.copyFileToDirectory(source, dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
