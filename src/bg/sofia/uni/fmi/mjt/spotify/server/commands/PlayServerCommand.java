package bg.sofia.uni.fmi.mjt.spotify.server.commands;

import bg.sofia.uni.fmi.mjt.spotify.common.models.CommandResponse;
import bg.sofia.uni.fmi.mjt.spotify.common.SpotifyCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import bg.sofia.uni.fmi.mjt.spotify.server.exceptions.PlaybackServiceException;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Song;
import bg.sofia.uni.fmi.mjt.spotify.server.threads.ServerStreamPlayback;
import bg.sofia.uni.fmi.mjt.spotify.server.services.PersistenceService;
import bg.sofia.uni.fmi.mjt.spotify.server.services.PlaybackService;
import com.google.gson.Gson;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PlayServerCommand implements SpotifyCommand {
    public static final String COMMAND_STRING = "play";
    private final String name;
    private final SpotifyServerInterface server;
    private static final Gson GSON = new Gson();

    public PlayServerCommand(String name, SpotifyServerInterface server) {
        this.name = name;
        this.server = server;
    }

    @Override
    public CommandResponse execute() {
        // for now get the first result
        Song song = server.getSongs().get(name).getFirst();
        Path path = Paths.get(PersistenceService.DATA_DIRECTORY + "/songs/" + song.sourceFilepath());

        ServerStreamPlayback serverStreamPlayback = new ServerStreamPlayback(path);
        server.setCurrentPlaybackThread(serverStreamPlayback);
        serverStreamPlayback.start();

        try {
            return new CommandResponse(GSON.toJson(
                PlaybackService.getAudioFormatSerializable(song)), true);
        } catch (PlaybackServiceException e) {
            // end socket connection
            return new CommandResponse("There was a problem while trying to play the song", false);
        }
    }
}
