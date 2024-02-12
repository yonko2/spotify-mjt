package bg.sofia.uni.fmi.mjt.spotify.server.services;

import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Song;

import java.util.Comparator;
import java.util.List;

public class AnalyticsService {
    public static List<Song> getMostListenedSongs(SpotifyServerInterface server, int number) {
        return server.getSongs().values().stream()
            .flatMap(List::stream)
            .sorted(Comparator.comparing(Song::getStreams).reversed())
            .limit(number)
            .toList();
    }
}
