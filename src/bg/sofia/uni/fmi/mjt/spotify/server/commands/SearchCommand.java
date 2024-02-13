package bg.sofia.uni.fmi.mjt.spotify.server.commands;

import bg.sofia.uni.fmi.mjt.spotify.common.models.CommandResponse;
import bg.sofia.uni.fmi.mjt.spotify.common.SpotifyCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Song;

import java.util.List;
import java.util.function.Predicate;

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
        Predicate<Song> songFilter = song -> {
            for (String keyword : keywords) {
                if (song.getTitle().toLowerCase().contains(keyword) ||
                    song.getAlbum().toLowerCase().contains(keyword) ||
                    song.getArtist().toLowerCase().contains(keyword)) {
                    return true;
                }
            }
            return false;
        };

        List<Song> songsList = server.getSongs().values().stream()
            .flatMap(List::stream)
            .filter(songFilter)
            .toList();

        if (songsList.isEmpty()) {
            return new CommandResponse("No results found", false);
        }

        StringBuilder result = new StringBuilder();
        for (Song song : songsList) {
            result.append(song.getSongInfo())
                .append(System.lineSeparator());
        }

        return new CommandResponse(result.toString(), true);
    }
}
