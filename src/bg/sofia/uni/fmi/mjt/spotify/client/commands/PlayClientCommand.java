package bg.sofia.uni.fmi.mjt.spotify.client.commands;

import bg.sofia.uni.fmi.mjt.spotify.common.CommandResponse;
import bg.sofia.uni.fmi.mjt.spotify.common.SpotifyCommand;

public class PlayClientCommand implements SpotifyCommand {
    public static final String COMMAND_STRING = "play";
    @Override
    public CommandResponse execute() {
        return null;
    }
}
