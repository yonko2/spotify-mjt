package bg.sofia.uni.fmi.mjt.spotify.server.commands;

import bg.sofia.uni.fmi.mjt.spotify.common.models.CommandResponse;
import bg.sofia.uni.fmi.mjt.spotify.common.SpotifyCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import bg.sofia.uni.fmi.mjt.spotify.server.services.AnalyticsService;

import java.util.List;

public class SearchCommand implements SpotifyCommand {
    public static final String COMMAND_STRING = "search";
    private static List<String> keywords;
    private final SpotifyServerInterface server;

    public SearchCommand(String words, SpotifyServerInterface server) {
        keywords = List.of(words.split("\\s+"));
        this.server = server;
    }

    @Override
    public CommandResponse execute() {
        String result = AnalyticsService.searchSongs(server, keywords);

        if (result.isEmpty()) {
            return new CommandResponse("No results found", false);
        }

        return new CommandResponse(result, true);
    }
}
