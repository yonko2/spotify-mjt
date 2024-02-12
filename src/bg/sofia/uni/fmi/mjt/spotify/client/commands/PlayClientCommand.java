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

    public PlayClientCommand(String audioFormatJson, SpotifyClientInterface spotifyClient) {
        this.audioFormatJson = audioFormatJson;
        this.spotifyClient = spotifyClient;
    }

    @Override
    public CommandResponse execute() {
        AudioFormatSerializable audioFormatSerializable = GSON.fromJson(audioFormatJson, AudioFormatSerializable.class);
        AudioFormat audioFormat = AudioFormatSerializable.toAudioFormat(audioFormatSerializable);

        ClientStreamPlayback clientStreamPlayback = new ClientStreamPlayback(audioFormat, spotifyClient);
        clientStreamPlayback.start();

        return new CommandResponse("Playback started on client", true);
    }
}
