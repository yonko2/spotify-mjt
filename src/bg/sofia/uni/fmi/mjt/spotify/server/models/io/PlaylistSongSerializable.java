package bg.sofia.uni.fmi.mjt.spotify.server.models.io;

import bg.sofia.uni.fmi.mjt.spotify.server.models.PlaylistSong;

import java.io.Serializable;
import java.util.UUID;

public record PlaylistSongSerializable(UUID songID, String timeAddedISOString) implements Serializable {
    public static PlaylistSongSerializable of(PlaylistSong song) {
        return new PlaylistSongSerializable(song.song().uuid(), song.timeAdded().toString());
    }
}
