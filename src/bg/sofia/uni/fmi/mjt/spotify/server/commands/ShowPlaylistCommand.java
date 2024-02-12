package bg.sofia.uni.fmi.mjt.spotify.server.commands;

import bg.sofia.uni.fmi.mjt.spotify.common.models.CommandResponse;
import bg.sofia.uni.fmi.mjt.spotify.common.SpotifyCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Playlist;
import bg.sofia.uni.fmi.mjt.spotify.server.models.User;
import bg.sofia.uni.fmi.mjt.spotify.server.services.PlaylistService;

import java.util.List;
import java.util.Map;

public class ShowPlaylistCommand implements SpotifyCommand {
    public static final String COMMAND_STRING = "show-playlist";
    private final String name;
    private final SpotifyServerInterface server;

    public ShowPlaylistCommand(String name, SpotifyServerInterface server) {
        this.name = name;
        this.server = server;
    }

    @Override
    public CommandResponse execute() {
        User owner = null;
        Playlist resultPlaylist = null;

        for (Map.Entry<User, List<Playlist>> kv : server.getPlaylists().entrySet()) {
            for (Playlist playlist : kv.getValue()) {
                if (playlist.name().equals(name)) {
                    owner = kv.getKey();
                    resultPlaylist = playlist;
                    break;
                }
            }
        }

        if (resultPlaylist == null) {
            return new CommandResponse("Playlist not found", false);
        } else {
            return new CommandResponse(PlaylistService.getPlaylistInfo(resultPlaylist, owner), true);
        }
    }
}
