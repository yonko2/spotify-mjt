package bg.sofia.uni.fmi.mjt.spotify.server.commands;

import bg.sofia.uni.fmi.mjt.spotify.common.CommandResponse;
import bg.sofia.uni.fmi.mjt.spotify.common.SpotifyCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;

import java.util.List;

public class SearchCommand implements SpotifyCommand {
    public static final String COMMAND_STRING = "search";
    private static List<String> keywords;

    public SearchCommand(String words, SpotifyServerInterface server) {
        keywords = List.of(words.split("\\s+"));
    }

    @Override
    public CommandResponse execute() {
        return null;
    }
}
