package bg.sofia.uni.fmi.mjt.spotify.server.commands;

import bg.sofia.uni.fmi.mjt.spotify.common.models.CommandResponse;
import bg.sofia.uni.fmi.mjt.spotify.common.SpotifyCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;

public class AddSongToPlaylistCommand implements SpotifyCommand {
    public static final String COMMAND_STRING = "add-song-to-playlist";
    private final String name;
    private final String song;
    private final SpotifyServerInterface server;

    public AddSongToPlaylistCommand(String name, String song, SpotifyServerInterface server) {
        this.name = name;
        this.song = song;
        this.server = server;
    }

    @Override
    public CommandResponse execute() {
        return null;
    }
}
