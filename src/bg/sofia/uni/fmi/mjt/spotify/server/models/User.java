package bg.sofia.uni.fmi.mjt.spotify.server.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record User(UUID uuid, String email,
                   String hashPass, List<Playlist> playlists) {
    public static User of(String email, String hashPass) {
        return new User(UUID.randomUUID(), email, hashPass, new ArrayList<>());
    }
}
