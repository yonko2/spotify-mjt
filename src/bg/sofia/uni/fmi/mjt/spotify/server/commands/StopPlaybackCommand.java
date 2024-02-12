package bg.sofia.uni.fmi.mjt.spotify.server.commands;

import bg.sofia.uni.fmi.mjt.spotify.common.models.CommandResponse;
import bg.sofia.uni.fmi.mjt.spotify.common.SpotifyCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;

public class StopPlaybackCommand implements SpotifyCommand {
    public static final String COMMAND_STRING = "stop";
    private final SpotifyServerInterface server;
    public StopPlaybackCommand(SpotifyServerInterface server) {
        this.server = server;
    }

    @Override
    public CommandResponse execute() {
        server.getCurrentPlaybackThread().interrupt();
        return new CommandResponse("Playback stopped", true);
    }
}
