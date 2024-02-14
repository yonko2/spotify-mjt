package bg.sofia.uni.fmi.mjt.spotify.server.commands;

import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import bg.sofia.uni.fmi.mjt.spotify.server.threads.ServerStreamPlayback;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.channels.SelectionKey;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StopPlaybackCommandTest {
    private static final SpotifyServerInterface server = mock(SpotifyServerInterface.class);
    private static final SelectionKey key = mock(SelectionKey.class);
    private static final ConcurrentHashMap<SelectionKey, ServerStreamPlayback> playbackThreads =
        new ConcurrentHashMap<>();
    private static final ServerStreamPlayback thread = mock(ServerStreamPlayback.class);

    @BeforeAll
    static void setUp() {
        playbackThreads.put(key, thread);
        when(server.getPlaybackThreads()).thenReturn(playbackThreads);
    }

    @Test
    void testExecuteSuccess() {
        var playbackThreadsCopy = new ConcurrentHashMap<>(playbackThreads);
        when(server.getPlaybackThreads()).thenReturn(playbackThreadsCopy);

        StopPlaybackCommand stopPlaybackCommand = new StopPlaybackCommand(server, key);
        var res = stopPlaybackCommand.execute();

        assertTrue(res.isSuccessful(), "Test stop playback success");
        assertTrue(playbackThreadsCopy.isEmpty(), "Test stop playback success");
    }

    @Test
    void testExecuteThrows() {
        SelectionKey keyCopy = mock(SelectionKey.class);

        StopPlaybackCommand stopPlaybackCommand = new StopPlaybackCommand(server, keyCopy);
        var res = stopPlaybackCommand.execute();

        assertFalse(res.isSuccessful(), "Test stop playback throws");
    }
}
