package bg.sofia.uni.fmi.mjt.spotify.server.services;

import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import bg.sofia.uni.fmi.mjt.spotify.server.exceptions.PasswordEncryptionException;
import bg.sofia.uni.fmi.mjt.spotify.server.exceptions.SessionServiceException;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Playlist;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Song;
import bg.sofia.uni.fmi.mjt.spotify.server.models.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.channels.SelectionKey;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {
    private static final SpotifyServerInterface serverMock = mock(SpotifyServerInterface.class);
    private static final List<Song> songsList = List.of(
        new Song(UUID.randomUUID(), "song1", "album1", "artist1", 1, 1, "src1"),
        new Song(UUID.randomUUID(), "song2", "album2", "artist2", 1, 5, "src2"),
        new Song(UUID.randomUUID(), "song3", "album3", "artist3", 1, 8, "src3"),
        new Song(UUID.randomUUID(), "song4", "album4", "artist4", 1, 4, "src4")
    );

    private static final List<Playlist> playlistsList = List.of(
        Playlist.of("playlist1"),
        Playlist.of("playlist2")
    );

    private static final List<User> usersList = List.of(
        new User(UUID.randomUUID(), "email1", "cc175b9c0f1b6a831c399e269772661", List.of(playlistsList.get(0))),
        new User(UUID.randomUUID(), "email2", "92eb5ffee6ae2fec3ad71c777531578f", List.of(playlistsList.get(1)))
    );

    private static final ConcurrentHashMap<String, List<Song>> songs = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<User, List<Playlist>> playlists = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<SelectionKey, User> selectionKeyToUser = new ConcurrentHashMap<>();
    private static final List<SelectionKey> selectionKeyList =
        List.of(mock(SelectionKey.class), mock(SelectionKey.class));

    @BeforeAll
    static void setUp() {
        when(serverMock.getSongs()).thenReturn(songs);
        when(serverMock.getUsers()).thenReturn(users);
        when(serverMock.getPlaylists()).thenReturn(playlists);
        when(serverMock.getSelectionKeyToUser()).thenReturn(selectionKeyToUser);

        songsList.forEach(song -> songs.put(song.getTitle(), List.of(song)));
        usersList.forEach(user -> users.put(user.email(), user));

        playlists.put(usersList.get(0), List.of(playlistsList.get(0)));
        playlists.put(usersList.get(1), List.of(playlistsList.get(1)));

        selectionKeyToUser.put(selectionKeyList.get(1), usersList.get(1));
    }

    @Test
    void testRegisterSuccess() {
        var usersCopy = new ConcurrentHashMap<>(users);
        when(serverMock.getUsers()).thenReturn(usersCopy);

        assertDoesNotThrow(() -> SessionService.register(usersCopy, "email3", "pass3"), "Test register success");
        assertEquals(3, usersCopy.size(), "Test register success");
    }

    @Test
    void testRegisterAlreadyExistsThrows() {
        assertThrows(IllegalArgumentException.class, () -> SessionService.register(users, "email1", "pass1"),
            "Test register already exists throws");
    }

    @Test
    void testLoginSuccess() {
        assertDoesNotThrow(() -> SessionService.login("email1", "a", serverMock, selectionKeyList.get(0)),
            "Test login success");
    }

    @Test
    void testLoginNoSuchUserThrows() {
        assertThrows(SessionServiceException.class,
            () -> SessionService.login("email5", "a", serverMock, selectionKeyList.get(0)),
            "Test login no such user throws");
    }

    @Test
    void testLoginAlreadyLoggedInThrows() {
        assertThrows(SessionServiceException.class,
            () -> SessionService.login("email2", "b", serverMock, selectionKeyList.get(1)),
            "Test login already logged in throws");
    }

    @Test
    void testLoginInvalidPasswordThrows() {
        assertThrows(SessionServiceException.class,
            () -> SessionService.login("email1", "b", serverMock, selectionKeyList.get(0)),
            "Test login invalid password throws");
    }
}
