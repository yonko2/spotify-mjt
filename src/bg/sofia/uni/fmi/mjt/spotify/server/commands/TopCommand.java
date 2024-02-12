package bg.sofia.uni.fmi.mjt.spotify.server.commands;

import bg.sofia.uni.fmi.mjt.spotify.common.models.CommandResponse;
import bg.sofia.uni.fmi.mjt.spotify.common.SpotifyCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Song;
import bg.sofia.uni.fmi.mjt.spotify.server.services.AnalyticsService;

public class TopCommand implements SpotifyCommand {
    public static final String COMMAND_STRING = "top";
    private final int number;
    private final SpotifyServerInterface server;

    public TopCommand(int number, SpotifyServerInterface server) {
        this.number = number;
        this.server = server;
    }

    @Override
    public CommandResponse execute() {
        var result = AnalyticsService.getMostListenedSongs(server, number);

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < result.size(); i++) {
            Song song = result.get(i);
            stringBuilder.append(i + 1).append(". ")
                .append(song.artist()).append(" - ")
                .append(song.title()).append(" - ")
                .append(song.streams()).append(" times played")
                .append(System.lineSeparator());
        }

        return new CommandResponse(stringBuilder.toString(), true);
    }
}
