package bg.sofia.uni.fmi.mjt.spotify.server.commands;

import bg.sofia.uni.fmi.mjt.spotify.common.models.CommandResponse;
import bg.sofia.uni.fmi.mjt.spotify.common.SpotifyCommand;

public class StopPlaybackCommand implements SpotifyCommand {
    public static final String COMMAND_STRING = "stop";
    @Override
    public CommandResponse execute() {
        return null;
    }
}
