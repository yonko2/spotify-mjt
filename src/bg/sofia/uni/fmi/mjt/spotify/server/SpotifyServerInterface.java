package bg.sofia.uni.fmi.mjt.spotify.server;

import bg.sofia.uni.fmi.mjt.spotify.server.models.Playlist;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Song;
import bg.sofia.uni.fmi.mjt.spotify.server.models.User;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public interface SpotifyServerInterface {
    ConcurrentHashMap<String, User> getUsers();

    List<Song> getSongs();

    ConcurrentHashMap<User, List<Playlist>> getPlaylists();

    User getLoggedUser();

    void start();

    void setLoggedUser(User user);
}
