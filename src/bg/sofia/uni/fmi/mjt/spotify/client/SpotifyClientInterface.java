package bg.sofia.uni.fmi.mjt.spotify.client;

import bg.sofia.uni.fmi.mjt.spotify.client.threads.ClientStreamPlayback;

public interface SpotifyClientInterface {
    void start();

    public ClientStreamPlayback getCurrentPlaybackThread();

    public void setCurrentPlaybackThread(ClientStreamPlayback currentPlaybackThread);
}
