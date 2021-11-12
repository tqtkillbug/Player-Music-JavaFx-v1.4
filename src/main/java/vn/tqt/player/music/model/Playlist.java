package vn.tqt.player.music.model;


import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private String name;
    private String path;
    private List<String> songs;

    public Playlist() {
        this.songs = new ArrayList<>();
    }

    public Playlist(String name, String path, List<String> songs) {
        this.name = name;
        this.path = path;
        this.songs = songs;
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

    public List<String> getSongs() {
        return songs;
    }

    public void setSongs(List<String> songs) {
        this.songs = songs;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Playlist) {
            return name.equals(((Playlist) o).name);
        }
        return false;
    }
}
