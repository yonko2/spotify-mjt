package bg.sofia.uni.fmi.mjt.spotify.server.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record Playlist(UUID uuid, String name, List<PlaylistSong> songList, LocalDateTime timeCreated) {
    public static Playlist of(String name) {
        return new Playlist(UUID.randomUUID(), name, new ArrayList<>(), LocalDateTime.now());
    }
}
