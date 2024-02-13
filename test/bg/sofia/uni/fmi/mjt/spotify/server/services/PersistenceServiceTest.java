package bg.sofia.uni.fmi.mjt.spotify.server.services;

import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServer;
import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PersistenceServiceTest {
    @Test
    void testLoadApplicationState() {
        SpotifyServerInterface server = new SpotifyServer();

        assertEquals(2, server.getUsers().size());
        assertEquals(2, server.getPlaylists().size());
        assertEquals(2, server.getSongs().size());
    }
}
