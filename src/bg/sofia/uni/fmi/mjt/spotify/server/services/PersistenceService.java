package bg.sofia.uni.fmi.mjt.spotify.server.services;

import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import bg.sofia.uni.fmi.mjt.spotify.server.exceptions.PersistenceServiceException;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Playlist;
import bg.sofia.uni.fmi.mjt.spotify.server.models.PlaylistSong;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Song;
import bg.sofia.uni.fmi.mjt.spotify.server.models.User;
import bg.sofia.uni.fmi.mjt.spotify.server.models.io.PlaylistSerializable;
import bg.sofia.uni.fmi.mjt.spotify.server.models.io.UserSerializable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PersistenceService {
    public static final String DATA_DIRECTORY = "src/bg/sofia/uni/fmi/mjt/spotify/server/data";
    public static final String USERS_PATH = DATA_DIRECTORY + "/users.json";
    public static final String SONGS_PATH = DATA_DIRECTORY + "/songs/songs.json";
    public static final String PLAYLISTS_PATH = DATA_DIRECTORY + "/playlists.json";
    private static final Object USERS_LOCK = new Object();
    private static final Object SONGS_LOCK = new Object();
    private static final Object PLAYLISTS_LOCK = new Object();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void loadApplicationState(SpotifyServerInterface spotifyServer) throws PersistenceServiceException {
        List<UserSerializable> userSerializableList = readUsersFromFile();
        List<PlaylistSerializable> playlistSerializableList = readPlaylistsFromFile();

        List<Song> songList = readSongsFromFile();
        List<Playlist> playlists =
            loadPlaylists(songList, playlistSerializableList);
        List<User> users = loadUsers(playlists, userSerializableList);

        users.forEach(user -> {
            spotifyServer.getUsers().put(user.email(), user);
            spotifyServer.getPlaylists().put(user, user.playlists());
        });

        songList.forEach(song -> {
            spotifyServer.getSongs().putIfAbsent(song.getTitle(), new ArrayList<>());
            spotifyServer.getSongs().get(song.getTitle()).add(song);
        });
    }

    public static void saveApplicationState(SpotifyServerInterface spotifyServer) throws PersistenceServiceException {
        List<UserSerializable> userSerializableList = spotifyServer.getUsers().values().stream()
            .map(UserSerializable::of).toList();
        List<PlaylistSerializable> playlistSerializableList = spotifyServer.getPlaylists().values().stream()
            .flatMap(Collection::stream)
            .map(PlaylistSerializable::of).toList();
        List<Song> songList = spotifyServer.getSongs().values().stream().flatMap(Collection::stream).toList();

        saveUsersToFile(userSerializableList);
        savePlaylistsToFile(playlistSerializableList);
        saveSongsToFile(songList);
    }

    private static <T> void saveClassToFile(List<T> data, String path, Object lock) throws PersistenceServiceException {
        synchronized (lock) {
            try (var writer = new FileWriter(path)) {
                GSON.toJson(data, writer);
            } catch (IOException e) {
                throw new PersistenceServiceException("Error while writing songs to file", e);
            }
        }
    }

    private static void saveSongsToFile(List<Song> songList) throws PersistenceServiceException {
        saveClassToFile(songList, SONGS_PATH, SONGS_LOCK);
    }

    private static void savePlaylistsToFile(List<PlaylistSerializable> playlistSerializableList)
        throws PersistenceServiceException {
        saveClassToFile(playlistSerializableList, PLAYLISTS_PATH, PLAYLISTS_LOCK);
    }

    private static void saveUsersToFile(List<UserSerializable> userSerializableList)
        throws PersistenceServiceException {
        saveClassToFile(userSerializableList, USERS_PATH, USERS_LOCK);
    }

    private static List<UserSerializable> readUsersFromFile() throws PersistenceServiceException {
        return readClassFromFile(UserSerializable.class, USERS_PATH, USERS_LOCK);
    }

    private static List<PlaylistSerializable> readPlaylistsFromFile() throws PersistenceServiceException {
        return readClassFromFile(PlaylistSerializable.class, PLAYLISTS_PATH, PLAYLISTS_LOCK);
    }

    private static List<Song> readSongsFromFile() throws PersistenceServiceException {
        return readClassFromFile(Song.class, SONGS_PATH, SONGS_LOCK);
    }

    private static <T> List<T> readClassFromFile(Class<T> tClass, String path, Object lock)
        throws PersistenceServiceException {
        synchronized (lock) {
            try (var reader = new FileReader(path)) {
                var res = GSON.fromJson(reader, Array.newInstance(tClass, 0).getClass());
                if (res == null) {
                    return List.of();
                }
                return Arrays.asList((T[]) res);
            } catch (IOException e) {
                throw new PersistenceServiceException("Error while reading data from file", e);
            }
        }
    }

    private static List<User> loadUsers(List<Playlist> playlists, List<UserSerializable> userSerializableList) {
        Map<UUID, Playlist> playlistIDMap = new HashMap<>();
        playlists.forEach(playlist -> playlistIDMap.put(playlist.uuid(), playlist));

        return userSerializableList.stream().map(userSerializable -> {
            List<Playlist> userPlaylists = new ArrayList<>(userSerializable.playlistIDs().stream()
                .map(playlistIDMap::get).toList());

            return new User(userSerializable.uuid(), userSerializable.email(), userSerializable.hashPass(),
                userPlaylists);
        }).toList();
    }

    private static List<Playlist> loadPlaylists(List<Song> songList,
                                                List<PlaylistSerializable> playlistSerializableList) {
        Map<UUID, Song> songIDMap = new HashMap<>();
        songList.forEach(song -> songIDMap.put(song.getUuid(), song));

        if (playlistSerializableList.isEmpty()) {
            return new ArrayList<>();
        }

        return playlistSerializableList.stream().map(playlistSerializable -> {
            List<PlaylistSong> playlistSongs = new ArrayList<>(playlistSerializable.songList().stream()
                .map(playlistSongSerializable -> new PlaylistSong(
                    songIDMap.get(playlistSongSerializable.songID()),
                    LocalDateTime.parse(playlistSongSerializable.timeAddedISOString())
                )).toList());

            return new Playlist(playlistSerializable.uuid(), playlistSerializable.name(), playlistSongs,
                LocalDateTime.parse(playlistSerializable.timeCreatedISOString()));
        }).toList();
    }
}
