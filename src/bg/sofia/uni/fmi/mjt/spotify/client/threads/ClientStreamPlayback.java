package bg.sofia.uni.fmi.mjt.spotify.client.threads;

import bg.sofia.uni.fmi.mjt.spotify.client.SpotifyClientInterface;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.io.BufferedInputStream;
import java.net.Socket;

public class ClientStreamPlayback extends Thread {
    private static final int BUFFER_SIZE = 4096;
    private static final String HOST = "localhost";
    private final AudioFormat audioFormat;
    private final SpotifyClientInterface spotifyClient;
    private final int port;
    private final Runnable resetDataLine;

    public ClientStreamPlayback(AudioFormat audioFormat, SpotifyClientInterface spotifyClient, int port,
                                Runnable resetDataLine) {
        this.audioFormat = audioFormat;
        this.spotifyClient = spotifyClient;
        this.port = port;
        this.resetDataLine = resetDataLine;
    }

    @Override
    public void run() {
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

        try (Socket socket = new Socket(HOST, port);
             SourceDataLine dataLine = (SourceDataLine) AudioSystem.getLine(info);
             AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                 new BufferedInputStream(socket.getInputStream()));
        ) {
            this.spotifyClient.setSourceDataLine(dataLine);

            dataLine.open();
            dataLine.start();

            if (socket.isConnected()) {
                byte[] bufferBytes = new byte[BUFFER_SIZE];
                int readBytes = -1;
                while ((readBytes = audioInputStream.read(bufferBytes)) != -1) {
                    dataLine.write(bufferBytes, 0, readBytes);
                }

                dataLine.drain();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        resetDataLine.run();
    }
}
