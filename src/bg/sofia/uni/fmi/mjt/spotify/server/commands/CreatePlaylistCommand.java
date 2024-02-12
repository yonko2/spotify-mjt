package bg.sofia.uni.fmi.mjt.spotify.server.commands;

import bg.sofia.uni.fmi.mjt.spotify.common.models.CommandResponse;
import bg.sofia.uni.fmi.mjt.spotify.common.SpotifyCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Playlist;

public class CreatePlaylistCommand implements SpotifyCommand {
    public static final String COMMAND_STRING = "create-playlist";
    private final String name;
    private final SpotifyServerInterface server;

    public CreatePlaylistCommand(String name, SpotifyServerInterface server) {
        this.name = name;
        this.server = server;
    }

    @Override
    public CommandResponse execute() {
        server.getPlaylists().get(server.getLoggedUser())
            .add(Playlist.of(name));

        return new CommandResponse("Playlist created successfully", true);
    }
}
