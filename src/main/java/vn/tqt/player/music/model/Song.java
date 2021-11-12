package vn.tqt.player.music.model;

import java.io.Serializable;

public class Song implements Serializable {
    private Integer id;
    private String name;
    private String path;

    public Song(){

    }
    public Song(Integer id, String name, String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

