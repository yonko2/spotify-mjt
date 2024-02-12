package bg.sofia.uni.fmi.mjt.spotify.server.services;

import bg.sofia.uni.fmi.mjt.spotify.common.models.io.AudioFormatSerializable;
import bg.sofia.uni.fmi.mjt.spotify.server.exceptions.PlaybackServiceException;
import bg.sofia.uni.fmi.mjt.spotify.server.models.Song;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

public class PlaybackService {
    private static final String SONGS_DIRECTORY = PersistenceService.DATA_DIRECTORY + "/songs/";

    public static AudioFormat getAudioFormat(Song song) throws PlaybackServiceException {
        try {
            return AudioSystem.getAudioInputStream(new File(SONGS_DIRECTORY + song.getSourceFilepath())).getFormat();
        } catch (IOException | UnsupportedAudioFileException e) {
            throw new PlaybackServiceException("A problem occurred when generating song format", e);
        }
    }

    public static AudioFormatSerializable getAudioFormatSerializable(Song song, int port)
        throws PlaybackServiceException {
        AudioFormat audioFormat = getAudioFormat(song);
        return AudioFormatSerializable.of(audioFormat, port);
    }

    public static int findFreePort() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        } catch (IOException e) {
            System.out.println("An error occurred while trying to find a free port.");
        }
        return -1;
    }
}
