package bg.sofia.uni.fmi.mjt.spotify.server;

import bg.sofia.uni.fmi.mjt.spotify.server.models.Playlist;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Song;
import bg.sofia.uni.fmi.mjt.spotify.server.models.User;
import bg.sofia.uni.fmi.mjt.spotify.server.threads.ServerStreamPlayback;

import java.nio.channels.SelectionKey;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public interface SpotifyServerInterface {
    ConcurrentMap<String, User> getUsers();

    ConcurrentMap<String, List<Song>> getSongs();

    ConcurrentMap<User, List<Playlist>> getPlaylists();

    ConcurrentMap<SelectionKey, User> getSelectionKeyToUser();

    void start();

    ConcurrentMap<SelectionKey, ServerStreamPlayback> getPlaybackThreads();
}
