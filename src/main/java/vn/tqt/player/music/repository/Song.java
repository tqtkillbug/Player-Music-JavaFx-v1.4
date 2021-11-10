package vn.tqt.player.music.repository;

import java.io.Serializable;

public class Song implements Serializable {
    private Integer id;
    private String songName;
    private String songPath;


    public Song(Integer id, String songName, String songPath) {
        this.id = id;
        this.songName = songName;
        this.songPath = songPath;
    }

    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    public Song(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }
}

