package bg.sofia.uni.fmi.mjt.spotify.server.commands;

import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Song;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchCommandTest {
    private static final List<Song> songsList = List.of(
        new Song(UUID.randomUUID(), "song1", "album1", "artist1", 1, 1, "src1"),
        new Song(UUID.randomUUID(), "song2", "album2", "artist2", 1, 5, "src2"),
        new Song(UUID.randomUUID(), "song3", "album3", "artist3", 1, 8, "src3"),
        new Song(UUID.randomUUID(), "song4", "album4", "artist4", 1, 4, "src4")
    );

    private static final SpotifyServerInterface serverMock = mock(SpotifyServerInterface.class);
    private static final ConcurrentHashMap<String, List<Song>> songs = new ConcurrentHashMap<>();

    @BeforeAll
    static void setUp() {
        when(serverMock.getSongs()).thenReturn(songs);
        songsList.forEach(song -> songs.put(song.getTitle(), List.of(song)));
    }

    @Test
    void testExecuteSuccess() {
        assertTrue(new SearchCommand("song1", serverMock).execute().message().contains("song1"), "Test search success");
    }

    @Test
    void testExecuteFails() {
        assertFalse(new SearchCommand("song5", serverMock).execute().message().contains("song5"),
            "Test search not found fails");
    }

}
