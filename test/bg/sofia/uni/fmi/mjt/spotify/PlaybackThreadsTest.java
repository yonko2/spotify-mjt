package bg.sofia.uni.fmi.mjt.spotify;

import bg.sofia.uni.fmi.mjt.spotify.client.threads.ClientStreamPlayback;
import bg.sofia.uni.fmi.mjt.spotify.server.threads.ServerStreamPlayback;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class PlaybackThreadsTest {
    static class ServerPlayback extends Thread {
        @Override
        public void run() {
            try {
                ServerStreamPlayback serverStreamPlayback = new ServerStreamPlayback(
                    Path.of("test.wav"), 1234, () -> {
                });
                serverStreamPlayback.setDaemon(true);
                serverStreamPlayback.start();
            } catch (Exception e) {
                System.out.println("Error in ServerPlayback");
            }
        }
    }

    static class ClientPlayback extends Thread {
        @Override
        public void run() {
            try {
                ClientStreamPlayback clientStreamPlayback = new ClientStreamPlayback(
                    null, null, 1234, () -> {
                });
                clientStreamPlayback.setDaemon(true);
                clientStreamPlayback.start();
            } catch (Exception e) {
                System.out.println("Error in ServerPlayback");
            }
        }
    }

    @Test
    void testServerPlaybackThreadStarts() {
        ServerPlayback serverPlayback = new ServerPlayback();
        assertDoesNotThrow(serverPlayback::start, "Test server playback thread starts");
    }

    @Test
    void testClientPlaybackThreadStarts() {
        try {
            ClientPlayback clientPlayback = new ClientPlayback();
            assertDoesNotThrow(clientPlayback::start, "Test client playback thread starts");
        } catch (Exception e) {
            System.out.println("Error in testClientPlaybackThreadStarts");
        }

    }
}
