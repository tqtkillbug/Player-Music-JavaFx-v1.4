package vn.tqt.player.music.services;

import vn.tqt.player.music.model.Playlist;
import vn.tqt.player.music.repository.IPlaylistRepository;

import java.util.List;

public interface IPlaylistService {

    public List<Playlist> getPlaylists();

    public void add(Playlist newPlaylist);

    public boolean exist(String name);

    public void update(Playlist newPlaylist);

    public void delete(Playlist newPlaylist);
}
