package bg.sofia.uni.fmi.mjt.spotify.server.commands;

import bg.sofia.uni.fmi.mjt.spotify.common.CommandResponse;
import bg.sofia.uni.fmi.mjt.spotify.common.SpotifyCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;

public class PlayServerCommand implements SpotifyCommand {
    public static final String COMMAND_STRING = "play";
    private final String name;
    private final SpotifyServerInterface server;

    public PlayServerCommand(String name, SpotifyServerInterface server) {
        this.name = name;
        this.server = server;
    }

    @Override
    public CommandResponse execute() {
        return null;
    }
}
