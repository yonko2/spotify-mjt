package bg.sofia.uni.fmi.mjt.spotify.server.services;

import bg.sofia.uni.fmi.mjt.spotify.server.models.Playlist;
import bg.sofia.uni.fmi.mjt.spotify.server.models.PlaylistSong;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Song;
import bg.sofia.uni.fmi.mjt.spotify.server.models.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PlaylistServiceTest {
    private static final FormatStyle FORMAT_STYLE = FormatStyle.MEDIUM;

    @Test
    void testGetPlaylistInfo() {
        LocalDateTime time = LocalDateTime.of(2024, 2, 13, 12, 22, 36);

        Song song = new Song(UUID.randomUUID(), "song1", "album1", "artist1", 1, 1, "src1");

        PlaylistSong playlistSong = new PlaylistSong(song, time);

        Playlist playlist = new Playlist(UUID.randomUUID(), "playlist1", List.of(PlaylistSong.of(song)),
            playlistSong.timeAdded());

        User user = new User(UUID.randomUUID(), "email1", "pass1", List.of(playlist));

        assertDoesNotThrow(() -> PlaylistService.getPlaylistInfo(playlist, user), "Test get playlist info");
    }
}
