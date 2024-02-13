package bg.sofia.uni.fmi.mjt.spotify.server.services;

import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServer;
import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Song;
import bg.sofia.uni.fmi.mjt.spotify.server.models.User;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PersistenceServiceTest {
    private static final String SONGS_PATH = "songs.json";

    @Test
    void testLoadApplicationState() {
        SpotifyServerInterface server = new SpotifyServer();

        Gson gson = new Gson();
        try (FileReader reader = new FileReader(SONGS_PATH)) {
            var songs = gson.fromJson(reader, Song[].class);
            if(songs == null) {
                songs = new Song[0];
            }
            assertEquals(songs.length, server.getSongs().size());
        } catch (IOException e) {
            Assertions.fail("Needed files not found (songs.json)");
        }

    }
}
