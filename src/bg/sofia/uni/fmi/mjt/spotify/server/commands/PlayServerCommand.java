package bg.sofia.uni.fmi.mjt.spotify.server.commands;

import bg.sofia.uni.fmi.mjt.spotify.common.models.CommandResponse;
import bg.sofia.uni.fmi.mjt.spotify.common.SpotifyCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import bg.sofia.uni.fmi.mjt.spotify.server.exceptions.PlaybackServiceException;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Song;
import bg.sofia.uni.fmi.mjt.spotify.server.threads.ServerStreamPlayback;
import bg.sofia.uni.fmi.mjt.spotify.server.services.PlaybackService;
import com.google.gson.Gson;

import java.nio.channels.SelectionKey;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PlayServerCommand implements SpotifyCommand {
    public static final String COMMAND_STRING = "play";
    private final String name;
    private final SpotifyServerInterface server;
    private final SelectionKey selectionKey;
    private static final Gson GSON = new Gson();

    public PlayServerCommand(String name, SpotifyServerInterface server, SelectionKey selectionKey) {
        this.name = name;
        this.server = server;
        this.selectionKey = selectionKey;
    }

    private void removeKey() {
        server.getPlaybackThreads().remove(selectionKey);
    }

    private final Runnable onFinish = this::removeKey;

    @Override
    public CommandResponse execute() {
        if (server.getPlaybackThreads().containsKey(selectionKey)) {
            return new CommandResponse("There is already a song playing", false);
        }

        int port = PlaybackService.findFreePort();

        if (!server.getSongs().containsKey(name)) {
            return new CommandResponse("Song not found", false);
        }

        // for now get the first result
        Song song = server.getSongs().get(name).getFirst();
        Path path = Paths.get("songs/" + song.getSourceFilepath());

        ServerStreamPlayback serverStreamPlayback = new ServerStreamPlayback(path, port, onFinish);
        server.getPlaybackThreads().put(selectionKey, serverStreamPlayback);
        serverStreamPlayback.start();

        song.increaseStreams();

        try {
            return new CommandResponse(GSON.toJson(
                PlaybackService.getAudioFormatSerializable(song, port)), true);
        } catch (PlaybackServiceException e) {
            return new CommandResponse("There was a problem while trying to play the song", false);
        }
    }
}
