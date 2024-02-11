package bg.sofia.uni.fmi.mjt.spotify.server.services;

import bg.sofia.uni.fmi.mjt.spotify.server.models.Playlist;
import bg.sofia.uni.fmi.mjt.spotify.server.models.PlaylistSong;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Song;
import bg.sofia.uni.fmi.mjt.spotify.server.models.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

public class PlaylistService {
    public static void addSongToPlaylist(Song song, Playlist playlist) {
        playlist.songList().add(new PlaylistSong(song, LocalDateTime.now()));
    }

    public static Playlist createPlaylist(String name) {
        return new Playlist(UUID.randomUUID(), name, new ArrayList<>(), LocalDateTime.now());
    }

    public static String getPlaylistInfo(Playlist playlist, User owner) {
        StringBuilder sb = new StringBuilder();
        sb.append("Playlist name: ");
        sb.append(playlist.name());
        sb.append("Author: ");
        sb.append(owner.email());
        sb.append(System.lineSeparator());
        sb.append("Songs count: ");
        sb.append(playlist.songList().size());
        sb.append(System.lineSeparator());
        sb.append("Created on: ");
        sb.append(playlist.timeCreated().format(DateTimeFormatter.BASIC_ISO_DATE));
        sb.append(System.lineSeparator());

        Optional<PlaylistSong> latestSong = getLatestDateTimeOfPlaylist(playlist);
        latestSong.ifPresent(song -> {
            sb.append("Last updated: ");
            sb.append(song.timeAdded().format(DateTimeFormatter.BASIC_ISO_DATE));
        });

        return sb.toString();
    }

    private static Optional<PlaylistSong> getLatestDateTimeOfPlaylist(Playlist playlist) {
        return playlist.songList().stream().max(Comparator.comparing(PlaylistSong::timeAdded));
    }
}
