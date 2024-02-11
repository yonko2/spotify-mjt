package bg.sofia.uni.fmi.mjt.spotify.server.models;

import java.time.LocalDateTime;

public record PlaylistSong(Song song, LocalDateTime timeAdded) {
    public static PlaylistSong of(Song song) {
        return new PlaylistSong(song, LocalDateTime.now());
    }
}
