package vn.tqt.player.music.services;

import vn.tqt.player.music.model.Playlist;
import vn.tqt.player.music.repository.IPlaylistRepository;
import vn.tqt.player.music.repository.PlaylistRepository;

import java.util.List;

public class PlaylistService implements IPlaylistService {

    IPlaylistRepository repository = new PlaylistRepository();

    @Override
    public List<Playlist> getPlaylists() {
        return repository.getPlaylists();
    }

    @Override
    public void add(Playlist newPlaylist) {

    }

    @Override
    public boolean exist(String name) {
        return false;
    }

    @Override
    public void update(Playlist newPlaylist) {

    }

    @Override
    public void delete(Playlist newPlaylist) {

    }
}
