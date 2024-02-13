package bg.sofia.uni.fmi.mjt.spotify.server.commands;

import bg.sofia.uni.fmi.mjt.spotify.common.models.CommandResponse;
import bg.sofia.uni.fmi.mjt.spotify.common.SpotifyCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import bg.sofia.uni.fmi.mjt.spotify.server.services.PlaylistService;

import java.nio.channels.SelectionKey;
import java.util.ArrayList;

public class CreatePlaylistCommand implements SpotifyCommand {
    public static final String COMMAND_STRING = "create-playlist";
    private final String name;
    private final SpotifyServerInterface server;
    private final SelectionKey selectionKey;

    public CreatePlaylistCommand(String name, SpotifyServerInterface server, SelectionKey selectionKey) {
        this.name = name;
        this.server = server;
        this.selectionKey = selectionKey;
    }

    @Override
    public CommandResponse execute() {
        server.getPlaylists().putIfAbsent(server.getSelectionKeyToUser().get(selectionKey), new ArrayList<>());
        server.getPlaylists().get(server.getSelectionKeyToUser().get(selectionKey))
            .add(PlaylistService.createPlaylist(name));

        return new CommandResponse("Playlist created successfully", true);
    }
}
