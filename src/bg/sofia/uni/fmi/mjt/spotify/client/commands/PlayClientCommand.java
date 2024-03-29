package bg.sofia.uni.fmi.mjt.spotify.client.commands;

import bg.sofia.uni.fmi.mjt.spotify.client.SpotifyClientInterface;
import bg.sofia.uni.fmi.mjt.spotify.client.threads.ClientStreamPlayback;
import bg.sofia.uni.fmi.mjt.spotify.common.models.CommandResponse;
import bg.sofia.uni.fmi.mjt.spotify.common.SpotifyCommand;
import bg.sofia.uni.fmi.mjt.spotify.common.models.io.AudioFormatSerializable;
import com.google.gson.Gson;

import javax.sound.sampled.AudioFormat;

public class PlayClientCommand implements SpotifyCommand {
    public static final String COMMAND_STRING = "play";
    private static final Gson GSON = new Gson();
    private final String audioFormatJson;
    private final SpotifyClientInterface spotifyClient;
    private final Runnable resetDataLine;

    public PlayClientCommand(String audioFormatJson, SpotifyClientInterface spotifyClient, Runnable resetDataLine) {
        this.audioFormatJson = audioFormatJson;
        this.spotifyClient = spotifyClient;
        this.resetDataLine = resetDataLine;
    }

    @Override
    public CommandResponse execute() {
        if (spotifyClient.getSourceDataLine() != null) {
            return new CommandResponse("Playback already started", false);
        }

        AudioFormatSerializable audioFormatSerializable = GSON.fromJson(audioFormatJson, AudioFormatSerializable.class);
        AudioFormat audioFormat = AudioFormatSerializable.toAudioFormat(audioFormatSerializable);

        ClientStreamPlayback clientStreamPlayback =
            new ClientStreamPlayback(audioFormat, spotifyClient, audioFormatSerializable.port(), resetDataLine);
        clientStreamPlayback.start();

        return new CommandResponse("Playback started on client", true);
    }
}
