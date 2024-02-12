package bg.sofia.uni.fmi.mjt.spotify.server.commands;

import bg.sofia.uni.fmi.mjt.spotify.common.models.CommandResponse;
import bg.sofia.uni.fmi.mjt.spotify.common.SpotifyCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;

import java.nio.channels.SelectionKey;

public class StopPlaybackCommand implements SpotifyCommand {
    public static final String COMMAND_STRING = "stop";
    private final SpotifyServerInterface server;
    private final SelectionKey selectionKey;

    public StopPlaybackCommand(SpotifyServerInterface server, SelectionKey selectionKey) {
        this.server = server;
        this.selectionKey = selectionKey;
    }

    @Override
    public CommandResponse execute() {
        if (!server.getPlaybackThreads().containsKey(selectionKey)) {
            return new CommandResponse("No playback is currently running", false);
        }

        server.getPlaybackThreads().remove(selectionKey);
        return new CommandResponse("Playback stopped", true);
    }
}
