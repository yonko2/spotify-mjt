package bg.sofia.uni.fmi.mjt.spotify.server.services;

import bg.sofia.uni.fmi.mjt.spotify.server.models.Song;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class PlaybackServiceTest {
    @Test
    void testGetAudioFormatSerializable() {
        Song song = new Song(UUID.randomUUID(), "artist", "album", "artist", 2, 3, "samsung.wav");

        assertDoesNotThrow(() -> PlaybackService.getAudioFormatSerializable(song, 8080));
    }
}
