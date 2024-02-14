package bg.sofia.uni.fmi.mjt.spotify.server.services;

import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Song;
import bg.sofia.uni.fmi.mjt.spotify.server.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {
    private static final SpotifyServerInterface serverMock = mock(SpotifyServerInterface.class);

    private static final List<Song> songsList = List.of(
        new Song(UUID.randomUUID(), "song1", "album1", "artist1", 1, 1, "src1"),
        new Song(UUID.randomUUID(), "song2", "album2", "artist2", 1, 5, "src2"),
        new Song(UUID.randomUUID(), "song3", "album3", "artist3", 1, 8, "src3"),
        new Song(UUID.randomUUID(), "song4", "album4", "artist4", 1, 4, "src4")
    );

    private static final ConcurrentHashMap<String, List<Song>> songs = new ConcurrentHashMap<>();

    @BeforeAll
    static void setUp() {
        when(serverMock.getSongs()).thenReturn(songs);

        songsList.forEach(song -> songs.put(song.getTitle(), List.of(song)));
    }

    @Test
    void testGetMostListenedSongsSuccess() {
        List<Song> mostListenedSongs = AnalyticsService.getMostListenedSongs(serverMock, 3);
        Assertions.assertEquals(3, mostListenedSongs.size(), "Test get most listened songs success");
        Assertions.assertEquals("song3", mostListenedSongs.get(0).getTitle(), "Test get most listened songs success");
        Assertions.assertEquals("song2", mostListenedSongs.get(1).getTitle(), "Test get most listened songs success");
        Assertions.assertEquals("song4", mostListenedSongs.get(2).getTitle(), "Test get most listened songs success");
    }

    @Test
    void testGetMostListenedSongsThrows() {
        assertThrows(IllegalArgumentException.class, () -> AnalyticsService.getMostListenedSongs(serverMock, 0),
            "Test get most listened songs throws");
    }

    @Test
    void testSearchSongs() {
        List<String> keywords = List.of("song1", "album2", "artist3");

        String result = AnalyticsService.searchSongs(serverMock, keywords);
        String expected = songs.get("song1").getFirst().getSongInfo() + System.lineSeparator() +
            songs.get("song2").getFirst().getSongInfo() + System.lineSeparator() +
            songs.get("song3").getFirst().getSongInfo() + System.lineSeparator();
        Assertions.assertEquals(expected, result, "Test search songs");
    }
}
