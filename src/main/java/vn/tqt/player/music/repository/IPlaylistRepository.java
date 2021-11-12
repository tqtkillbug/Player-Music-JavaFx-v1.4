package vn.tqt.player.music.repository;

import vn.tqt.player.music.model.Playlist;

import java.util.List;

public interface IPlaylistRepository {
    List<Playlist> getPlaylists();

    Playlist getById();

    Playlist getByName(String name);

    void add(Playlist newPlaylist);

    boolean exist(String name);

    void update(Playlist newPlaylist);

    void update(List<Playlist> list);

    void delete(Playlist newPlaylist);
}
