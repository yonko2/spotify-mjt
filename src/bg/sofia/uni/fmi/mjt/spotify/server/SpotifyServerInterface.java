package bg.sofia.uni.fmi.mjt.spotify.server;

import bg.sofia.uni.fmi.mjt.spotify.server.models.Playlist;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Song;
import bg.sofia.uni.fmi.mjt.spotify.server.models.User;
import bg.sofia.uni.fmi.mjt.spotify.server.threads.ServerStreamPlayback;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public interface SpotifyServerInterface {
    ConcurrentHashMap<String, User> getUsers();

    ConcurrentHashMap<String, List<Song>> getSongs();

    ConcurrentHashMap<User, List<Playlist>> getPlaylists();

    User getLoggedUser();

    void start();

    void setLoggedUser(User user);

    public ServerStreamPlayback getCurrentPlaybackThread();

    public void setCurrentPlaybackThread(ServerStreamPlayback currentPlaybackThread);
}
