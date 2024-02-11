package bg.sofia.uni.fmi.mjt.spotify.server.models.io;

import bg.sofia.uni.fmi.mjt.spotify.server.models.Playlist;
import bg.sofia.uni.fmi.mjt.spotify.server.models.User;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public record UserSerializable(UUID uuid, String email,
                               String hashPass, List<UUID> playlistIDs) implements Serializable {
    public static UserSerializable of(User user) {
        return new UserSerializable(user.uuid(), user.email(), user.hashPass(),
            user.playlists().stream().map(Playlist::uuid).toList());
    }
}
