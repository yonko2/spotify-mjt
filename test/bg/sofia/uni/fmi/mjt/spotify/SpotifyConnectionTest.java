package bg.sofia.uni.fmi.mjt.spotify;

import bg.sofia.uni.fmi.mjt.spotify.client.SpotifyClient;
import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SpotifyConnectionTest {
    static class ConnectionRunnable implements Runnable {
        @Override
        public void run() {
            try {
                SpotifyClient client = new SpotifyClient();
                client.start();
                client.disconnect();
            } catch (Exception e) {
                System.out.println("Error in connection runnable test");
            }
        }
    }

    static class ServerRunnable implements Runnable {
        @Override
        public void run() {
            try {
                SpotifyServer server = new SpotifyServer();
                server.start();
                server.shutdown();
            } catch (Exception e) {
                System.out.println("Error in server runnable test");
            }
        }
    }

    @Test
    void testStartClient() {
        Thread thread = new Thread(new ConnectionRunnable());
        thread.setDaemon(true);
        assertDoesNotThrow(thread::start, "Test start client");
    }

    @Test
    void testStartServer() {
        Thread thread = new Thread(new ServerRunnable());
        thread.setDaemon(true);
        assertDoesNotThrow(thread::start, "Test start server");
    }
}
