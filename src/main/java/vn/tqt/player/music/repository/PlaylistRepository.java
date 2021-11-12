package vn.tqt.player.music.repository;

import vn.tqt.player.music.model.Playlist;
import vn.tqt.player.music.utils.JacksonParser;
import vn.tqt.player.music.utils.TextFileUtil;

import java.util.List;

public class PlaylistRepository implements IPlaylistRepository {
    private final static String filePath = "data/playlist.json";
    @Override
    public List<Playlist> getPlaylists() {
        return JacksonParser.INSTANCE.toList(TextFileUtil.read(filePath), Playlist.class);
    }

    @Override
    public Playlist getById() {
        return null;
    }

    @Override
    public Playlist getByName(String name) {
        for (Playlist playlist : getPlaylists()) {
            if (playlist.getName().equals(name))
                return playlist;
        }
        return null;
    }

    @Override
    public void add(Playlist newPlaylist) {
        List<Playlist> playlists = getPlaylists();
        if (!exist(newPlaylist.getName())) {
            playlists.add(newPlaylist);
        }
        String json = JacksonParser.INSTANCE.toJson(playlists);
        TextFileUtil.writeFile(json, filePath);
    }

    @Override
    public boolean exist(String name) {
        return getByName(name) != null;
    }

    @Override
    public void update(Playlist newPlaylist) {

    }

    @Override
    public void update(List<Playlist> list) {
        String json = JacksonParser.INSTANCE.toJson(list);
        TextFileUtil.writeFile(json, filePath);
    }

    @Override
    public void delete(Playlist newPlaylist) {

    }
}
