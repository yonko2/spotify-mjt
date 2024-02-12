package bg.sofia.uni.fmi.mjt.spotify.client.commands;

import bg.sofia.uni.fmi.mjt.spotify.common.models.CommandResponse;
import bg.sofia.uni.fmi.mjt.spotify.common.SpotifyCommand;
import bg.sofia.uni.fmi.mjt.spotify.common.models.io.AudioFormatSerializable;
import com.google.gson.Gson;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class PlayClientCommand implements SpotifyCommand {
    public static final String COMMAND_STRING = "play";
    private static final Gson GSON = new Gson();
    private String audioFormatJson;

    public PlayClientCommand(String audioFormatJson) {
        this.audioFormatJson = audioFormatJson;
    }

    @Override
    public CommandResponse execute() {
        AudioFormatSerializable audioFormatSerializable = GSON.fromJson(audioFormatJson, AudioFormatSerializable.class);
        AudioFormat audioFormat = AudioFormatSerializable.toAudioFormat(audioFormatSerializable);

        try (Socket socket = new Socket("localhost", 6666)) {
            if (socket.isConnected()) {
                InputStream in = new BufferedInputStream(socket.getInputStream());
                play(in);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // receive song info from server
        // connect to socket in new thread to play the song
        return null;
    }

    private static synchronized void play(final InputStream in) throws Exception {
        AudioInputStream ais = AudioSystem.getAudioInputStream(in);
        try (Clip clip = AudioSystem.getClip()) {
            clip.open(ais);
            clip.start();
            Thread.sleep(100); // given clip.drain a chance to start
            clip.drain();
        }
    }
}
