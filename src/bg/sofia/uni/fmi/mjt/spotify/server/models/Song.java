package bg.sofia.uni.fmi.mjt.spotify.server.models;

import java.util.UUID;

public record Song(UUID uuid, String title, String album, String artist,
                   int timeSeconds, long streams, String sourceFilepath) {
    public static Song of(String title, String album, String artist, int timeSeconds, String sourceFilepath) {
        return new Song(UUID.randomUUID(), title, album, artist, timeSeconds, 0, sourceFilepath);
    }
}
