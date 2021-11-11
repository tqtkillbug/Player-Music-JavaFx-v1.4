package vn.tqt.player.music.repository;


import java.util.ArrayList;
import java.util.List;

public class Playlist {
    public static List<Playlist> listPlaylists = new ArrayList<Playlist>();
    private String name;
    private String path;
    private List<String> listSong;

    public Playlist(String name, String path, List<String> listSong) {
        this.name = name;
        this.path = path;
        this.listSong = listSong;
    }

    public Playlist() {
        this.listSong = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getListSong() {
        return listSong;
    }

    public void setListSong(List<String> listSong) {
        this.listSong = listSong;
    }
}
