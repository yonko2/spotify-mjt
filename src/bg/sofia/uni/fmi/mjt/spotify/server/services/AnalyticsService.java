package bg.sofia.uni.fmi.mjt.spotify.server.services;

import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Song;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class AnalyticsService {
    public static List<Song> getMostListenedSongs(SpotifyServerInterface server, int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("Number must be positive");
        }

        return server.getSongs().values().stream()
            .flatMap(List::stream)
            .sorted(Comparator.comparing(Song::getStreams).reversed())
            .limit(number)
            .toList();
    }

    public static String searchSongs(SpotifyServerInterface server, List<String> keywords) {
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

        StringBuilder result = new StringBuilder();
        for (Song song : songsList) {
            result.append(song.getSongInfo())
                .append(System.lineSeparator());
        }
        return result.toString();
    }
}
