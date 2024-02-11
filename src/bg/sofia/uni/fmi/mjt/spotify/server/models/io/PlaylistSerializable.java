package bg.sofia.uni.fmi.mjt.spotify.server.models.io;

import bg.sofia.uni.fmi.mjt.spotify.server.models.Playlist;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public record PlaylistSerializable(UUID uuid, String name,
                                   List<PlaylistSongSerializable> songList, String timeCreatedISOString)
    implements Serializable {

    public static PlaylistSerializable of(Playlist playlist) {
        return new PlaylistSerializable(playlist.uuid(), playlist.name(),
            playlist.songList().stream().map(PlaylistSongSerializable::of).toList(),
            playlist.timeCreated().toString());
    }
}
