package bg.sofia.uni.fmi.mjt.spotify.server.commands;

import bg.sofia.uni.fmi.mjt.spotify.common.models.CommandResponse;
import bg.sofia.uni.fmi.mjt.spotify.common.SpotifyCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Playlist;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Song;
import bg.sofia.uni.fmi.mjt.spotify.server.services.PlaylistService;

import java.util.Collection;

public class AddSongToPlaylistCommand implements SpotifyCommand {
    public static final String COMMAND_STRING = "add-song-to";
    private final String name;
    private final String song;
    private final SpotifyServerInterface server;

    public AddSongToPlaylistCommand(String name, String song, SpotifyServerInterface server) {
        this.name = name;
        this.song = song;
        this.server = server;
    }

    @Override
    public CommandResponse execute() {
        Song songObject = server.getSongs().values().stream().flatMap(Collection::stream)
            .filter(s -> s.getTitle().equals(song))
            .findFirst().orElse(null);

        if (songObject == null) {
            return new CommandResponse("Song not found", false);
        }

        Playlist playlistObject = server.getPlaylists().values().stream().flatMap(Collection::stream)
            .filter(playlist -> playlist.name().equals(name))
            .findFirst().orElse(null);

        if (playlistObject == null) {
            return new CommandResponse("Playlist not found", false);
        }

        PlaylistService.addSongToPlaylist(songObject, playlistObject);
        return new CommandResponse("Song added to playlist successfully", true);
    }
}
