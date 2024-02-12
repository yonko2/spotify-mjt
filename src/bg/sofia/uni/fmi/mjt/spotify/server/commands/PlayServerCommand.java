package bg.sofia.uni.fmi.mjt.spotify.server.commands;

import bg.sofia.uni.fmi.mjt.spotify.common.models.CommandResponse;
import bg.sofia.uni.fmi.mjt.spotify.common.SpotifyCommand;
import bg.sofia.uni.fmi.mjt.spotify.server.SpotifyServerInterface;
import bg.sofia.uni.fmi.mjt.spotify.server.exceptions.PlaybackServiceException;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Song;
import bg.sofia.uni.fmi.mjt.spotify.server.services.PersistenceService;
import bg.sofia.uni.fmi.mjt.spotify.server.services.PlaybackService;
import com.google.gson.Gson;

import javax.sound.sampled.AudioFormat;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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

        try (ServerSocket serverSocket = new ServerSocket(6666);
             FileInputStream in = new FileInputStream(
                 PersistenceService.DATA_DIRECTORY + "songs" + song.sourceFilepath())) {
            if (serverSocket.isBound()) {
                Socket client = serverSocket.accept();
                OutputStream out = client.getOutputStream();

                byte[] buffer = new byte[2048];
                int count;
                while ((count = in.read(buffer)) != -1)
                    out.write(buffer, 0, count);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            return new CommandResponse(GSON.toJson(
                PlaybackService.getAudioFormatSerializable(song)
            ), true);
        } catch (PlaybackServiceException e) {
            // end socket connection
            return new CommandResponse("There was a problem while trying to play the song", false);
        }
    }
}
