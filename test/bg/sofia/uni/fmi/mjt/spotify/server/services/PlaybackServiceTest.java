package bg.sofia.uni.fmi.mjt.spotify.server.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PlaybackServiceTest {
    @Test
    void testFindFreePortSuccess() {
        var port = PlaybackService.findFreePort();
        assertTrue(port > 0, "Test find free port success");
    }
}
